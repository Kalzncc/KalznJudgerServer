package com.kalzn.judger.service.judgehandle.factory;

import com.google.gson.Gson;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.config.ResultConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component("resultConfigFactory")
public class ResultConfigFactory {

    @Autowired
    private JudgerServerConfig serverConfig;

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ResultConfig createResultConfigFromJsonStr(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, ResultConfig.class);
    }


    public ResultConfig createResultConfigFromJsonFile(File file) throws IOException {
        if (file.exists() && file.isFile()) {
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
            return createResultConfigFromJsonStr(stringBuffer.toString());

        } else {
            throw new IOException("Json file is not found.");
        }
    }

    public  ResultConfig createResultConfigFromJsonFile(String path) throws IOException {
        return createResultConfigFromJsonFile(new File(path));
    }
}
