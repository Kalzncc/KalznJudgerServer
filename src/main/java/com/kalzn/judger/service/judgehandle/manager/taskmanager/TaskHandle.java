package com.kalzn.judger.service.judgehandle.manager.taskmanager;

import com.kalzn.judger.service.judgehandle.callback.Acceptable;
import com.kalzn.judger.service.judgehandle.config.JudgerConfig;
import com.kalzn.judger.service.judgehandle.config.TaskRequestConfig;

import java.util.Queue;

public interface TaskHandle extends Acceptable {
    void pause();
    void restart();
    Thread start();
    int getStatus();
    void waitAllTask() throws InterruptedException;
    Queue<JudgerConfig> shutdown() throws InterruptedException;
    void submit(TaskRequestConfig requestConfig) throws InterruptedException;
}
