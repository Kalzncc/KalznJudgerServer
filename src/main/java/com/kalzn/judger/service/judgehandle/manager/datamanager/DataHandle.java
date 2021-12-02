package com.kalzn.judger.service.judgehandle.manager.datamanager;

import java.io.IOException;

public interface DataHandle {
    void pushData(int dataID) throws Exception;
    Long releaseData(int dataID) throws Exception;
    Integer getStatus();
    void clearData();
    Thread start();
    void pause();
    void restart();
    void registerData(int dataID);
    int registerData() throws IOException;
    void resetData();
    void shutdown() throws InterruptedException;
}
