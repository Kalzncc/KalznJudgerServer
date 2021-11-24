package com.kalzn.datamanager;

import com.kalzn.config.JudgerDataConfig;
import com.kalzn.config.JudgerServerConfig;
import com.kalzn.datamanager.dbcon.JudgerDataBase;
import com.kalzn.datamanager.inter.DataRequest;
import com.kalzn.util.FileIO;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataManager implements DataRequest {
    private  Map<Integer, ReentrantReadWriteLock> dataLocks;
    private JudgerServerConfig judgerServerConfig;

    public void setDataLocks(Map<Integer, ReentrantReadWriteLock> dataLocks) {
        this.dataLocks = dataLocks;
    }

    public void setJudgerServerConfig(JudgerServerConfig judgerServerConfig) {
        this.judgerServerConfig = judgerServerConfig;
    }

    public void registerData(int dataID) {
        dataLocks.put(dataID, new ReentrantReadWriteLock());
    }

    public boolean checkData(int dataID) throws IOException {
        if (dataLocks.get(dataID) == null) {
            return false;
        } else {
            dataLocks.get(dataID).readLock().lock();
            boolean ans = false;
            try {
                ans = (new JudgerDataBase(judgerServerConfig)).checkDataEnable(dataID);
            } finally {
                dataLocks.get(dataID).writeLock().unlock();
            }

            return ans;
        }
    }

    public void pushData(int dataID) throws RuntimeException, IOException {
        if (dataLocks.get(dataID) == null) {
            throw new RuntimeException("Data do not registered");
        }
        dataLocks.get(dataID).writeLock().lock();
        try {
            JudgerDataBase judgerDataBase = new JudgerDataBase(judgerServerConfig);
            if (!judgerDataBase.checkDataEnable(dataID)) {
                judgerDataBase.updateData(dataID);
            }
        } finally {
            dataLocks.get(dataID).writeLock().unlock();
        }

    }
    public void releaseData(int dataID) throws RuntimeException, IOException {
        if (dataLocks.get(dataID) == null) {
            throw new RuntimeException("Data do not registered");
        }
        dataLocks.get(dataID).writeLock().lock();
        try {
            (new JudgerDataBase(judgerServerConfig)).releaseData(dataID);
        } finally {
            dataLocks.get(dataID).writeLock().unlock();
        }

    }




    @Override
    public String requestData(int dataID, int compilerID) throws IOException {
        JudgerDataBase judgerDataBase = new JudgerDataBase(judgerServerConfig);
        if (!judgerDataBase.checkDataEnable(dataID)) {
            judgerDataBase.updateData(dataID);
        }
        return judgerServerConfig.getJudgerDataSpacePath()+"/"+dataID+ "/" + compilerID + "/" + "index.json";
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
}
