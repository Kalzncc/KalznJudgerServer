package com.kalzn.factory;

import com.kalzn.config.JudgerServerConfig;
import com.kalzn.datamanager.DataManager;
import com.kalzn.datamanager.dbcon.JudgerDataBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataManagerFactory {
    public static DataManager createDataManager(JudgerServerConfig config) throws IOException {
        DataManager dataManager = new DataManager();
        dataManager.setJudgerServerConfig(config);
        Map<Integer, ReentrantReadWriteLock> mp = new HashMap<>();
        dataManager.setDataLocks(mp);
        JudgerDataBase judgerDataBase = new JudgerDataBase(config);
        Integer[] dataIDs = judgerDataBase.pushAllEnableDataID();
        for (var id : dataIDs) {
            dataManager.registerData(id);
        }
        return dataManager;
    }
}
