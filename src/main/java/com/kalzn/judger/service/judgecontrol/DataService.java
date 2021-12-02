package com.kalzn.judger.service.judgecontrol;

import java.util.List;

public interface DataService {
    void updateData(Integer dataID) throws Exception;
    void clearDataSpace() throws Exception;
    void resetData();
    Long releaseData(Integer dataID) throws Exception;
    Integer checkManagerStatus();
    void pauseService() throws RuntimeException;
    void startService() throws RuntimeException;
}
