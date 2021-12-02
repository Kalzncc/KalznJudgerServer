package com.kalzn.judger.service.judgecontrol.support;

import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.manager.codemanager.CodeHandle;
import com.kalzn.judger.service.judgehandle.manager.datamanager.DataHandle;
import com.kalzn.judger.service.judgehandle.manager.datamanager.impl.DataManager;
import com.kalzn.judger.service.judgehandle.manager.taskmanager.TaskHandle;
import com.kalzn.judger.service.judgehandle.manager.taskmanager.impl.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component("serverManager")
public class ServerManager {
    @Autowired
    private JudgerServerConfig serverConfig;
    @Autowired
    private TaskHandle taskManager;
    @Autowired
    private DataHandle dataManager;
    @Autowired
    private CodeHandle codeManager;
    @Autowired
    private ReentrantLock serviceLock;


    public void closeService() throws InterruptedException {
        serviceLock.lock();
        try {
            if (serverConfig.getServiceStatus() == JudgerServerConfig.SERVICE_ENABLE) {
                serverConfig.setServiceStatus(JudgerServerConfig.SERVICE_NOT_ENABLE);
                taskManager.pause();
                taskManager.waitAllTask();
                dataManager.pause();
            }
        } catch (Exception e) {
            openService();
            throw e;
        } finally {
            serviceLock.unlock();
        }
    }

    public void openService() throws InterruptedException {
        serviceLock.lock();
        try {
            if (serverConfig.getServiceStatus() == JudgerServerConfig.SERVICE_NOT_ENABLE) {
                if (dataManager.getStatus() == DataManager.PREPARING)
                    dataManager.start();
                else
                    dataManager.restart();
                if (taskManager.getStatus() == TaskManager.PREPARING)
                    taskManager.start();
                else
                    taskManager.restart();
                serverConfig.setServiceStatus(JudgerServerConfig.SERVICE_ENABLE);
            }
        } catch (Exception e) {
            closeService();
            throw e;
        }finally {
            serviceLock.unlock();
        }
    }

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void setTaskManager(TaskHandle taskManager) {
        this.taskManager = taskManager;
    }

    public void setDataManager(DataHandle dataManager) {
        this.dataManager = dataManager;
    }

    public void setCodeManager(CodeHandle codeManager) {
        this.codeManager = codeManager;
    }

    public void setServiceLock(ReentrantLock serviceLock) {
        this.serviceLock = serviceLock;
    }
}
