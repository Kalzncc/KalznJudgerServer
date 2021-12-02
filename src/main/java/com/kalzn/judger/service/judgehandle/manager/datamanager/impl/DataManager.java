package com.kalzn.judger.service.judgehandle.manager.datamanager.impl;

import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.manager.datamanager.DataHandle;
import com.kalzn.judger.service.judgehandle.manager.datamanager.DataRequest;
import com.kalzn.judger.leaderserver.LeaderServer;
import com.kalzn.judger.util.FileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Component("dataManger")
public class DataManager implements DataRequest, DataHandle, Runnable {


    private  Map<Integer, ReentrantReadWriteLock> dataLocks;
    private ReentrantLock runLock;
    private Condition running;

    @Autowired
    private JudgerServerConfig serverConfig;

    @Autowired
    private LeaderServer leaderServer;

    private static class DataUse implements Comparable<DataUse> {
        public Integer dataID;
        public Date lastUseDate;
        public DataUse(Integer dataID) {
            this.dataID = dataID;
        }
        public DataUse(Integer dataID, Date lastUseDate) {
            this.dataID = dataID;
            this.lastUseDate = lastUseDate;
        }
        @Override
        public int compareTo(DataUse dataUse) {
            if (this.dataID == dataUse.dataID) return 0;
            else if (this.dataID > dataUse.dataID) return 1;
            else return -1;
        }
    }
    private Set<DataUse> dataUseTable;

    public static final int PREPARING = 0;
    public static final int RUNNING = 1;
    public static final int PAUSING = 2;
    public static final int SHUTDOWN = 3;
    private volatile Integer status;

    private Thread thisThread;

    @PostConstruct
    public void initConstruct() throws IOException{
        status = PREPARING;
        dataUseTable = new TreeSet<DataUse>();
        runLock = new ReentrantLock();
        running = runLock.newCondition();
        Map<Integer, ReentrantReadWriteLock> mp = new HashMap<>();
        setDataLocks(mp);
    }
    @Override
    public Integer getStatus() {
        return status;
    }

    private void activateData(int dataID) {
        DataUse tmp = new DataUse(dataID);
        dataUseTable.remove(tmp);
        dataUseTable.add(new DataUse(dataID, new Date()));
    }
    private void deleteData(int dataID) {
        dataUseTable.remove(new DataUse(dataID) );

    }


    public void setDataLocks(Map<Integer, ReentrantReadWriteLock> dataLocks) {
        this.dataLocks = dataLocks;
    }

    public void setJudgerServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void registerData(int dataID) {
        dataLocks.put(dataID, new ReentrantReadWriteLock());
    }

    @Override
    public int registerData() throws IOException {
        Integer[] dataIDs = leaderServer.pushAllEnableDataID();
        for (var id : dataIDs) {
            dataLocks.put(id, new ReentrantReadWriteLock());
        }
        return dataIDs.length;
    }

    @Override
    public void resetData() {
        if (status == RUNNING) {
            throw new RuntimeException("Manager is running");
        }

        FileIO.delete(serverConfig.getJudgerDataSpacePath());
        File space = new File(serverConfig.getJudgerDataSpacePath());
        space.mkdirs();
        dataUseTable.clear();

    }

    public boolean checkData(int dataID) throws IOException {
        if (dataLocks.get(dataID) == null) {
            return false;
        } else {
            dataLocks.get(dataID).readLock().lock();
            boolean ans = false;
            try {
                ans = leaderServer.checkDataEnable(dataID);
            } finally {
                dataLocks.get(dataID).writeLock().unlock();
            }
            return ans;
        }
    }

    @Override
    public void pushData(int dataID) throws RuntimeException, IOException {
        if (dataLocks.get(dataID) == null) {
            throw new RuntimeException("Data do not registered");
        }
        activateData(dataID);
        dataLocks.get(dataID).writeLock().lock();
        try {
            if (!leaderServer.checkDataEnable(dataID)) {
                leaderServer.updateData(dataID);
            }
        } finally {
            dataLocks.get(dataID).writeLock().unlock();
        }

    }
    @Override
    public Long releaseData(int dataID) throws RuntimeException, IOException {
        if (dataLocks.get(dataID) == null) {
            throw new RuntimeException("Data do not registered");
        }
        dataLocks.get(dataID).writeLock().lock();
        long res = 0;
        try {
            res = leaderServer.releaseData(dataID);
        } finally {
            dataLocks.get(dataID).writeLock().unlock();
        }
        deleteData(dataID);
        return res;
    }



    @Override
    public void clearData() {
        Long curDataSpaceSize = FileIO.checkSize(new File(serverConfig.getJudgerDataSpacePath()));
        if (curDataSpaceSize > (long)serverConfig.getDataSpaceMaxSize() * 1024 * 1024) {
            DataUse[] dataUses =  (DataUse[]) dataUseTable.toArray(new DataUse[dataUseTable.size()]);
            if (dataUses == null) return;
            Arrays.sort(dataUses, new Comparator<DataUse>() {
                @Override
                public int compare(DataUse dataUse, DataUse t1) {
                    return dataUse.lastUseDate.compareTo(t1.lastUseDate);
                }
            });
            try {
                for (int i = 0; i < dataUses.length && curDataSpaceSize > (long)serverConfig.getDataSpaceClearSize() * 1024 * 1024; i++) {
                    curDataSpaceSize -= releaseData(dataUses[i].dataID);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public String requestData(int dataID, int compilerID) throws IOException {
        activateData(dataID);
        if (!leaderServer.checkDataEnable(dataID)) {
            leaderServer.updateData(dataID);
        }
        return serverConfig.getJudgerDataSpacePath()+"/"+dataID+ "/" + compilerID + "/" + "index.json";
    }

    @Override
    public void report(String taskID) throws IOException {
        leaderServer.reportTask(taskID);
    }

    @Override
    public void lockRead(int dataID) {
        if (dataLocks.get(dataID) == null) {
            throw new RuntimeException("Data do not registered");
        }
        dataLocks.get(dataID).writeLock().lock();
    }
    @Override
    public void unlockRead(int dataID) {
        if (dataLocks.get(dataID) == null) {
            throw new RuntimeException("Data do not registered");
        }
        dataLocks.get(dataID).writeLock().unlock();
    }


    @Override
    public Thread start() {
        if (status != PREPARING) throw new RuntimeException("manager not preparing");
        status = RUNNING;
        thisThread = new Thread(this);
        thisThread.start();
        return thisThread;
    }
    @Override
    public void pause() {
        if (status != RUNNING) throw new RuntimeException("manager not running");
        status = PAUSING;
    }
    @Override
    public void restart() {
        if (status != PAUSING) throw new RuntimeException("manager not pausing");
        status = RUNNING;
        runLock.lock();
        try {
            running.signalAll();
        } finally {
            runLock.unlock();
        }
        thisThread.interrupt();
    }
    @Override
    public void shutdown() throws InterruptedException {
        status = SHUTDOWN;
        if (thisThread == null) return;
        thisThread.interrupt();
        thisThread.join();
    }



    private void mainService() throws InterruptedException {
        while(status != SHUTDOWN) {
            runLock.lock();
            try {
                while(status == PAUSING)
                    running.await();
            } finally {
                runLock.unlock();
            }

            clearData();

            long time = (long)serverConfig.getDataSpaceSizeCheckTime() * 60 * 1000;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) { }
        }
    }
    @Override
    public void run() {
        try {
            mainService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
