package com.kalzn.judger.service.judgehandle.callback;

import com.kalzn.judger.service.judgehandle.config.TaskConfig;

import java.io.IOException;

public interface Acceptable {
    void accept(TaskConfig taskConfig) throws IOException;
}
