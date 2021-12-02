package com.kalzn.judger.service.judgehandle.factory;

import com.google.gson.Gson;
import com.kalzn.judger.service.judgehandle.config.JudgerDataConfig;
import com.kalzn.judger.config.JudgerServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component("judgerDataSetFactory")
public class JudgerDataSetFactory {

    @Autowired
    private JudgerServerConfig serverConfig;

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public JudgerDataConfig[] createJudgerDataSetFromJsonStr(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, JudgerDataConfig[].class);
    }


    public JudgerDataConfig[] createJudgerDataSetFromJsonFile(File file) throws IOException {
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
            return createJudgerDataSetFromJsonStr(stringBuffer.toString());

        } else {
            throw new IOException("Json file is not found.");
        }
    }

    public  JudgerDataConfig[] createJudgerDataSetFromJsonFile(String path) throws IOException {
        return createJudgerDataSetFromJsonFile(new File(path));
    }
}
