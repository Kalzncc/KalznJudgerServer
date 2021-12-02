package com.kalzn.judger.service.judgecontrol.impl;

import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.manager.datamanager.DataHandle;
import com.kalzn.judger.service.judgecontrol.ConfigService;
import com.kalzn.judger.service.judgecontrol.support.ServerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private JudgerServerConfig serverConfig;
    @Autowired
    private ServerManager serverManager;
    @Autowired
    private DataHandle dataManager;

    @Override
    public void configServer(String scheme, String leaderIp, Integer port, Integer serverId) throws InterruptedException, IOException {
        serverManager.closeService();
        serverConfig.setServerId(serverId);
        serverConfig.setLeaderServerIp(leaderIp);
        serverConfig.setLeaderServerUrl(scheme+"://"+leaderIp+":"+port);
        dataManager.resetData();
        dataManager.registerData();
        serverManager.openService();
    }

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
