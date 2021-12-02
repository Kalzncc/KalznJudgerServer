package com.kalzn.judger.service.judgecontrol.impl;

import com.kalzn.judger.service.judgehandle.manager.datamanager.DataHandle;
import com.kalzn.judger.service.judgehandle.manager.datamanager.impl.DataManager;
import com.kalzn.judger.service.judgecontrol.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dataService")
public class DataServiceImpl implements DataService {

    @Autowired
    private DataHandle dataManager;

    @Override
    public void updateData(Integer dataID) throws Exception {
        dataManager.pushData(dataID);

    }
    @Override
    public void clearDataSpace() {
        dataManager.clearData();
    }

    @Override
    public void resetData() {
        dataManager.resetData();
    }

    @Override
    public Long releaseData(Integer dataID) throws Exception {
        return dataManager.releaseData(dataID);
    }

    @Override
    public Integer checkManagerStatus() {
        return dataManager.getStatus();
    }

    @Override
    public void pauseService() throws RuntimeException {
        dataManager.pause();
    }

    @Override
    public void startService() throws RuntimeException {
        if (dataManager.getStatus() == DataManager.PREPARING)
            dataManager.start();
        else
            dataManager.restart();
    }

    public void setDataManager(DataHandle dataManager) {
        this.dataManager = dataManager;
    }
}
