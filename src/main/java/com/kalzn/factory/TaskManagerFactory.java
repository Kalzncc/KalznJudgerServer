package com.kalzn.factory;

import com.kalzn.callback.Acceptable;
import com.kalzn.config.JudgerConfig;
import com.kalzn.config.JudgerServerConfig;
import com.kalzn.config.TaskConfig;
import com.kalzn.datamanager.inter.DataRequest;
import com.kalzn.taskmanager.TaskManager;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TaskManagerFactory {
    public static TaskManager createTaskManager(JudgerServerConfig serverConfig, DataRequest dataRequest) {
        return createTaskManager(serverConfig, dataRequest, null);
    }
    public static TaskManager createTaskManager(JudgerServerConfig serverConfig, DataRequest dataRequest, Acceptable leader) {
        TaskManager manager = new TaskManager();
        manager.setServerConfig(serverConfig);
        Queue<JudgerConfig> queue =  new LinkedList<>();
        manager.setWaitQueue(queue);
        manager.setDataRequest(dataRequest);
        if (serverConfig.getResultAcceptWay() == JudgerServerConfig.CALLED) {
            Queue<TaskConfig> doenQueue = new LinkedList<>();
            manager.setDoneQueue(doenQueue);
            manager.setDoneQueueLock(new ReentrantLock());
        } else {
            if (leader == null) {
                manager.setLeader(new Acceptable() {
                    @Override
                    public void accept(TaskConfig taskConfig) {
                        throw new RuntimeException("No recipient");
                    }
                });
            } else {
                manager.setLeader(leader);
            }
        }
        ReentrantLock lock = new ReentrantLock();
        manager.setQueueLock(lock);
        manager.setQueueEmpty(lock.newCondition());
        manager.setQueueFull(lock.newCondition());

        ArrayBlockingQueue<Runnable> threadQueue = new ArrayBlockingQueue<Runnable>(serverConfig.getMinActiveTaskThreadPoolNumber());
        ThreadPoolExecutor executor = new ThreadPoolExecutor(serverConfig.getMinActiveTaskThreadPoolNumber(), serverConfig.getMaxTaskRunningSize(), serverConfig.getTaskThreadActiveTime(), TimeUnit.MILLISECONDS, threadQueue);
        manager.setRunBoxPoolExecutor(executor);
        return manager;
    }
}
