package com.kalzn.judger.service.judgehandle.manager.datamanager;

import java.io.IOException;

public interface DataRequest {
    String requestData(int dataID, int compilerID) throws IOException;
    void report(String taskID) throws IOException;
    void lockRead(int dataID);
    void unlockRead(int dataID);
}
