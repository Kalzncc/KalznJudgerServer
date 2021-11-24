package com.kalzn.factory;

import com.google.gson.Gson;
import com.kalzn.config.JudgerConfig;
import com.kalzn.config.JudgerServerConfig;
import com.kalzn.config.TaskRequestConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JudgerConfigFactory {
    JudgerServerConfig config;
    public JudgerConfigFactory(JudgerServerConfig config) {
        this.config = config;
    }

    public JudgerConfig createJudgerConfigFromJsonStr(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, JudgerConfig.class);
    }


    public JudgerConfig createJudgerConfigFromJsonFile(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            if (file.length() > config.getMaxJsonFileSize()) {
                throw new IOException("Json file is too large. (size is" + file.length()  +" that exceed" + config.getMaxJsonFileSize()  + " )");
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
        judgerconfig.setMaxCharBuffer(config.getMaxBufferChar());
        judgerconfig.setJudgeMode(requestConfig.getJudgeMode());
        judgerconfig.setiOMode(requestConfig.getiOMode());
        judgerconfig.setStrictMode(requestConfig.getStrictMode());
        judgerconfig.setWorkSpacePath(config.getJudgerWorkSpace());
        judgerconfig.setResultPath("result.json");
        judgerconfig.setLogPath(config.getJudgerCoreLogPath());
        judgerconfig.setsPJ(requestConfig.issPJ());
        if (requestConfig.issPJ()) {
            judgerconfig.setSpjExePath(config.getJudgerDataSpacePath()+"/"+requestConfig.getDataID()+"/spj");
            judgerconfig.setSpjExeName("spj");
            judgerconfig.setMaxSPJTime(config.getMaxSPJTime());
            judgerconfig.setMaxSPJMemory(config.getMaxSPJMemory());
        }
        judgerconfig.setDataID(requestConfig.getDataID());
        judgerconfig.setTranslator(config.getTranslators().get(requestConfig.getCompilerID()));
        judgerconfig.setCodeSourcePath(config.getJudgerCodeSpacePath()+"/"+requestConfig.getTaskID()+"/"+judgerconfig.getTranslator().getCodeFileName());
        judgerconfig.setCodeFileName(judgerconfig.getTranslator().getCodeFileName());

        return judgerconfig;
    }
}
