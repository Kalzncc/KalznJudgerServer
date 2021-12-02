package com.kalzn.judger.service.judgecontrol;

import java.io.IOException;

public interface TaskService {
    void submit(String requestStr, String code) throws InterruptedException, IOException;



}
