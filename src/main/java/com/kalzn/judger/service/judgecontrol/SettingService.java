package com.kalzn.judger.service.judgecontrol;

public interface SettingService {
    String hello();
    void registerData(int dataID);
    Integer checkManagerStatus();
    void pauseService() throws InterruptedException;
    void startService() throws InterruptedException;
}
