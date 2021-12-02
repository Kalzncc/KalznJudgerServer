package com.kalzn.judger.service.judgehandle.manager.taskmanager.impl;

import com.kalzn.judger.service.judgehandle.manager.taskmanager.TaskHandle;
import com.kalzn.judger.service.judgehandle.config.JudgerConfig;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.config.TaskConfig;
import com.kalzn.judger.service.judgehandle.config.TaskRequestConfig;
import com.kalzn.judger.service.judgehandle.factory.JudgerConfigFactory;
import com.kalzn.judger.service.judgehandle.runbox.Runbox;
import com.kalzn.judger.service.judgehandle.callback.Acceptable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


@Component("taskManager")
public class TaskManager implements Runnable, TaskHandle {
    private Queue<JudgerConfig> waitQueue;
    private Queue<TaskConfig> doneQueue;

    private ReentrantLock queueLock;
    private ReentrantLock doneQueueLock;
    private Condition queueFull;
    private Condition queueEmpty;
    private Thread thisThread;
    private ThreadPoolExecutor runBoxPoolExecutor;

    @Autowired
    @Qualifier("leaderServer")
    private Acceptable leader;


    public static final int PREPARING = 0;
    public static final int RUNNING = 1;
    public static final int PAUSING = 2;
    private static final int SHUTDOWN = 3;
    volatile Integer status;
    private Integer runCnt;
    private ReentrantLock runLock;
    private Condition runFull;
    private Condition runEmpty;

    private ReentrantLock pauseLock;
    private Condition pausing;


    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private JudgerServerConfig serverConfig;

    @Autowired
    private JudgerConfigFactory judgerConfigFactory;
    @PostConstruct
    public void initConstruct() {
        status = PREPARING;
        Queue<JudgerConfig> queue =  new LinkedList<>();
        setWaitQueue(queue);
        Queue<TaskConfig> doenQueue = new LinkedList<>();
        setDoneQueue(doenQueue);
        setDoneQueueLock(new ReentrantLock());
        ReentrantLock lock = new ReentrantLock();
        setQueueLock(lock);
        ArrayBlockingQueue<Runnable> threadQueue = new ArrayBlockingQueue<Runnable>(serverConfig.getMinActiveTaskThreadPoolNumber());
        ThreadPoolExecutor executor = new ThreadPoolExecutor(serverConfig.getMinActiveTaskThreadPoolNumber(), serverConfig.getMaxTaskRunningSize(), serverConfig.getTaskThreadActiveTime(), TimeUnit.MILLISECONDS, threadQueue);
        setRunBoxPoolExecutor(executor);
    }



    private void init() {
        runLock = new ReentrantLock();
        runFull = runLock.newCondition();
        runEmpty = runLock.newCondition();
        runCnt = 0;

        pauseLock = new ReentrantLock();
        pausing = pauseLock.newCondition();
    }
    @Override
    public void pause() {
        if (status != RUNNING) throw new RuntimeException("Manager is not running");
        status = PAUSING;
    }
    @Override
    public void restart() {
        if (status != PAUSING) throw new RuntimeException("Manager is not pausing");
        status = RUNNING;
        pauseLock.lock();
        try {
            pausing.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
    @Override
    public Thread start() {
        if (status != PREPARING) throw new RuntimeException("Manager is not preparing");
        status = RUNNING;
        init();
        thisThread = new Thread(this);
        thisThread.start();
        return thisThread;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void waitAllTask() throws InterruptedException{
        if (status != PAUSING) {
            throw new RuntimeException("Manager is not pausing");
        }
        runLock.lock();
        try {
            while(runCnt != 0 || waitQueue.size() != 0) {
                runEmpty.await();
            }
        } finally {
            runLock.unlock();
        }
    }

    @Override
    public Queue<JudgerConfig> shutdown() throws InterruptedException {
        if (status == SHUTDOWN) throw new RuntimeException("Manager has exit");
        status = SHUTDOWN;
        thisThread.interrupt();
        thisThread.join();
        runBoxPoolExecutor.shutdown();
        return waitQueue;
    }
    @Override
    public void submit(TaskRequestConfig requestConfig) throws InterruptedException {
        if (status == SHUTDOWN)
            throw new RuntimeException("Manager has exit");
        queueLock.lock();
        try {
            while (waitQueue.size() >= serverConfig.getMaxTaskWaitingSize())
                queueEmpty.await();
            JudgerConfig judgerConfig = judgerConfigFactory.createJudgerConfigFromRequest(requestConfig);
            waitQueue.add(judgerConfig);
            queueFull.signalAll();
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
            pauseLock.lock();
            try {
                while (status == PAUSING && waitQueue.size() == 0)
                    pausing.await();
            } finally {
                pauseLock.unlock();
            }
            JudgerConfig judgerConfig = null;
            queueLock.lock();
            try {
                while (waitQueue.size() == 0) queueFull.await();
                if (waitQueue.size() != 0) {
                    judgerConfig = waitQueue.remove();
                    queueEmpty.signalAll();
                }
            } catch (InterruptedException e) {
                break;
            } finally {
                queueLock.unlock();
            }
            runLock.lock();
            try {
                while (runCnt >= serverConfig.getMaxTaskRunningSize())
                    runFull.await();
                runCnt++;
                TaskConfig taskConfig = new TaskConfig(judgerConfig.getTaskID(), judgerConfig);
                Runbox runbox = (Runbox) applicationContext.getBean("runbox");
                runbox.pushTask(taskConfig);
                runBoxPoolExecutor.execute(runbox);

            } finally {
                runLock.unlock();
            }
        }

        runLock.lock();
        try {
            while(runCnt != 0) runEmpty.await();
        } finally {
            runLock.unlock();
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


    public void setQueueLock(ReentrantLock queueLock) {
        queueEmpty = queueLock.newCondition();
        queueFull = queueLock.newCondition();
        this.queueLock = queueLock;
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
    public void accept(TaskConfig taskConfig) throws IOException {
        runLock.lock();
        try {
            runCnt--;
            runFull.signalAll();
            if (runCnt == 0)
                runEmpty.signalAll();
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
