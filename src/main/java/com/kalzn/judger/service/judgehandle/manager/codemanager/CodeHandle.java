package com.kalzn.judger.service.judgehandle.manager.codemanager;

import java.io.IOException;

public interface CodeHandle {
    void saveCode(String codeID, int compilerID, String code) throws IOException;
    void deleteCode(String codeID) throws IOException;
}
