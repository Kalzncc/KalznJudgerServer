package com.kalzn.taskmanager;

import com.kalzn.callback.Acceptable;
import com.kalzn.config.JudgerConfig;
import com.kalzn.config.JudgerServerConfig;
import com.kalzn.config.TaskConfig;
import com.kalzn.config.TaskRequestConfig;
import com.kalzn.datamanager.inter.DataRequest;
import com.kalzn.factory.JudgerConfigFactory;
import com.kalzn.runbox.Runbox;

import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskManager implements Runnable, Acceptable {
    private Queue<JudgerConfig> waitQueue;

    private Queue<TaskConfig> doneQueue;

    private ThreadPoolExecutor runBoxPoolExecutor;

    private JudgerServerConfig serverConfig;

    private ReentrantLock queueLock;

    private ReentrantLock doneQueueLock;

    private Condition queueFull;
    private Condition queueEmpty;

    private Thread thisThread;

    private Acceptable leader;

    private DataRequest dataRequest;

    public static final int PREPARING = 0;
    public static final int RUNNING = 1;
    public static final int PAUSING = 2;
    private static final int SHUTDOWN = 3;
    volatile Integer status;
    volatile Integer runCnt;
    private ReentrantLock runLock;
    private Condition runFull;
    private void init() {
        runLock = new ReentrantLock();
        runFull = runLock.newCondition();
        runCnt = 0;
    }

    public void pause() {
        if (status != RUNNING) throw new RuntimeException("Manager is not running");
        status = PAUSING;
    }
    public void restart() {
        if (status != PAUSING) throw new RuntimeException("Manager is not pausing");
    }
    public void start() {
        if (status != PREPARING) throw new RuntimeException("Manager is not preparing");
        status = RUNNING;
        init();
        thisThread = new Thread(this);
        thisThread.start();
    }
    public void shutdown() throws InterruptedException {
        if (status == SHUTDOWN) throw new RuntimeException("Manager has exit");
        status = SHUTDOWN;
        thisThread.join();
        runBoxPoolExecutor.shutdown();
    }
    public void submit(TaskRequestConfig requestConfig) throws InterruptedException {
        if (status == SHUTDOWN)
            throw new RuntimeException("Manager has exit");
        queueLock.lock();
        try {
            while (waitQueue.size() >= serverConfig.getMaxTaskWaitingSize())
                queueEmpty.await();
            JudgerConfig judgerConfig = (new JudgerConfigFactory(serverConfig)).createJudgerConfigFromRequest(requestConfig);
            waitQueue.add(judgerConfig);
            queueFull.notifyAll();
        } finally {
            queueLock.unlock();
        }
    }

    public void setLeader(Acceptable leader) {
        this.leader = leader;
    }

    public TaskConfig getDoneTask() {
        if (serverConfig.getResultAcceptWay() == JudgerServerConfig.SUBMIT)
            throw new RuntimeException("The way of submitting result is not been called");
        doneQueueLock.lock();
        TaskConfig doneTask = null;
        try {
            if (doneQueue.size() > 0) doneTask = doneQueue.remove();
        } finally {
            doneQueueLock.unlock();
        }
        return doneTask;
    }

    private void mainService() throws InterruptedException {
        while(status == RUNNING || status == PAUSING) {
            while(status == PAUSING);
            JudgerConfig judgerConfig = null;
            queueLock.lock();
            try {
                while(waitQueue.size() == 0) queueFull.await();
                judgerConfig = waitQueue.remove();
                queueEmpty.notifyAll();
            } finally {
                queueLock.unlock();
            }

            runLock.lock();
            try {
                while(runCnt >= serverConfig.getMaxTaskRunningSize())
                    runFull.await();
                runCnt++;
                TaskConfig taskConfig = new TaskConfig(judgerConfig.getTaskID(), judgerConfig);
                Runbox runbox = new Runbox(taskConfig,this,serverConfig,dataRequest);
                runBoxPoolExecutor.execute(runbox);
            } finally {
                runLock.unlock();
            }

        }
    }

    @Override
    public void run()  {
        try {
            mainService();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setDataRequest(DataRequest dataRequest) {
        this.dataRequest = dataRequest;
    }

    public void setQueueLock(ReentrantLock queueLock) {
        this.queueLock = queueLock;
    }

    public void setQueueFull(Condition queueFull) {
        this.queueFull = queueFull;
    }

    public void setQueueEmpty(Condition queueEmpty) {
        this.queueEmpty = queueEmpty;
    }

    public void setWaitQueue(Queue<JudgerConfig> waitQueue) {
        this.waitQueue = waitQueue;
    }

    public void setRunBoxPoolExecutor(ThreadPoolExecutor runBoxPoolExecutor) {
        this.runBoxPoolExecutor = runBoxPoolExecutor;
    }

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void setDoneQueue(Queue<TaskConfig> doneQueue) {
        this.doneQueue = doneQueue;
    }

    public void setDoneQueueLock(ReentrantLock doneQueueLock) {
        this.doneQueueLock = doneQueueLock;
    }

    @Override
    public void accept(TaskConfig taskConfig) {
        runLock.lock();
        try {
            runCnt--;
            runFull.notifyAll();
        } finally {
            runLock.unlock();
        }
        if (serverConfig.getResultAcceptWay() == JudgerServerConfig.SUBMIT) {
            leader.accept(taskConfig);
        } else {
            doneQueueLock.lock();
            try {
                doneQueue.add(taskConfig);
            } finally {
                doneQueueLock.unlock();
            }
        }
    }
}
