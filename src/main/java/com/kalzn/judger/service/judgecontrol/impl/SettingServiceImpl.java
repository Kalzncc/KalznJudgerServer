package com.kalzn.judger.service.judgecontrol.impl;

import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.manager.codemanager.CodeHandle;
import com.kalzn.judger.service.judgehandle.manager.datamanager.DataHandle;
import com.kalzn.judger.service.judgehandle.manager.taskmanager.TaskHandle;
import com.kalzn.judger.service.judgecontrol.SettingService;
import com.kalzn.judger.service.judgecontrol.support.ServerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("settingService")
public class SettingServiceImpl implements SettingService {

    @Autowired
    private JudgerServerConfig serverConfig;
    @Autowired
    private TaskHandle taskManager;
    @Autowired
    private DataHandle dataManager;
    @Autowired
    private CodeHandle codeManager;
    @Autowired
    private ServerManager serverManager;


    @Override
    public String hello() {
        return serverConfig.toJson();
    }

    @Override
    public void registerData(int dataID) {
        dataManager.registerData(dataID);
    }

    @Override
    public Integer checkManagerStatus() {
        return serverConfig.getServiceStatus();
    }


    @Override
    public void pauseService() throws InterruptedException {
        serverManager.closeService();
    }

    @Override
    public void startService() throws InterruptedException {
        serverManager.openService();
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
    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }
}
