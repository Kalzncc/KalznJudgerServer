package com.kalzn.datamanager.inter;

import java.io.IOException;

public interface DataRequest {
    String requestData(int dataID, int compilerID) throws IOException;
    void lockRead(int dataID);
    void unlockRead(int dataID);
}
