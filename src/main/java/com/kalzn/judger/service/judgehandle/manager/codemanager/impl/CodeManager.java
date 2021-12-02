package com.kalzn.judger.service.judgehandle.manager.codemanager.impl;

import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.manager.codemanager.CodeHandle;

import com.kalzn.judger.service.judgehandle.manager.codemanager.CodeRelease;
import com.kalzn.judger.util.FileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@Component("codeManager")
public class CodeManager implements CodeHandle, CodeRelease {
    @Autowired
    private JudgerServerConfig serverConfig;

    @Override
    public void saveCode(String codeID, int compilerID, String code) throws IOException{
        String codePath = serverConfig.getJudgerCodeSpacePath() + "/" + codeID;
        File codePathDir = new File(codePath);
        if (codePathDir.exists()) throw new IOException("Code is exists");
        codePathDir.mkdirs();
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(codePath + "/" + serverConfig.getTranslators().get(compilerID).getCodeFileName()));
            writer.write(code);
        } finally {
            writer.close();
        }
    }

    @Override
    public void deleteCode(String codeID) {
        String codePath = serverConfig.getJudgerCodeSpacePath() + "/" + codeID;
        File codePathDir = new File(codePath);
        if (codePathDir.exists()) {
            FileIO.delete(codePathDir);
        }
    }

    @Override
    public void releaseTask(String taskID) {
        deleteCode(taskID);
    }
}
