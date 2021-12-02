package com.kalzn.judger.service.judgecontrol;

import java.io.IOException;

public interface ConfigService {
    void configServer(String Scheme, String leaderIp, Integer port, Integer serverId) throws InterruptedException, IOException;
}
