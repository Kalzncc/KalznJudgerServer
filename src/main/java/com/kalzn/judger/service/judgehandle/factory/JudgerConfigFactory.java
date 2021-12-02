package com.kalzn.judger.service.judgehandle.factory;

import com.google.gson.Gson;
import com.kalzn.judger.service.judgehandle.config.JudgerConfig;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.config.TaskRequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component("judgerConfigFactory")
public class JudgerConfigFactory {

    @Autowired
    private JudgerServerConfig serverConfig;

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public JudgerConfig createJudgerConfigFromJsonStr(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, JudgerConfig.class);
    }


    public JudgerConfig createJudgerConfigFromJsonFile(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            if (file.length() > serverConfig.getMaxJsonFileSize()) {
                throw new IOException("Json file is too large. (size is" + file.length()  +" that exceed" + serverConfig.getMaxJsonFileSize()  + " )");
            }
            FileReader reader = null;
            StringBuffer stringBuffer = null;
            try {
                reader = new FileReader(file);
                stringBuffer = new StringBuffer();
                int ch;
                while ((ch = reader.read()) != -1) {
                    stringBuffer.append((char)ch);
                }
            } finally {
                reader.close();
            }
            return createJudgerConfigFromJsonStr(stringBuffer.toString());

        } else {
            throw new IOException("Json file is not found.");
        }
    }

    public  JudgerConfig createJudgerConfigFromJsonFile(String path) throws IOException {
        return createJudgerConfigFromJsonFile(new File(path));
    }

    public JudgerConfig createJudgerConfigFromRequest(TaskRequestConfig requestConfig) {
        JudgerConfig judgerconfig = new JudgerConfig();
        judgerconfig.setTaskID(requestConfig.getTaskID());
        judgerconfig.setMaxCharBuffer(serverConfig.getMaxBufferChar());
        judgerconfig.setJudgeMode(requestConfig.getJudgeMode());
        judgerconfig.setiOMode(requestConfig.getiOMode());
        judgerconfig.setStrictMode(requestConfig.getStrictMode());
        judgerconfig.setWorkSpacePath(serverConfig.getJudgerWorkSpace());
        judgerconfig.setResultPath("result.json");
        judgerconfig.setLogPath(serverConfig.getJudgerCoreLogPath());
        judgerconfig.setsPJ(requestConfig.issPJ());
        if (requestConfig.issPJ()) {
            judgerconfig.setSpjExePath(serverConfig.getJudgerDataSpacePath()+"/"+requestConfig.getDataID()+"/spj");
            judgerconfig.setSpjExeName("spj");
            judgerconfig.setMaxSPJTime(serverConfig.getMaxSpjTime());
            judgerconfig.setMaxSPJMemory(serverConfig.getMaxSpjMemory());
        }
        judgerconfig.setDataID(requestConfig.getDataID());
        judgerconfig.setTranslator(serverConfig.getTranslators().get(requestConfig.getCompilerID()));
        judgerconfig.setCodeSourcePath(serverConfig.getJudgerCodeSpacePath()+"/"+requestConfig.getTaskID()+"/"+judgerconfig.getTranslator().getCodeFileName());
        judgerconfig.setCodeFileName(judgerconfig.getTranslator().getCodeFileName());

        return judgerconfig;
    }
}
