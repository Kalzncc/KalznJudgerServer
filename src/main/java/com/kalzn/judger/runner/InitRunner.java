package com.kalzn.judger.runner;

import com.kalzn.judger.JudgerApplication;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.util.TokenMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


@Component
public class InitRunner implements ApplicationRunner {

    @Autowired
    private JudgerServerConfig serverConfig;

    private static final Logger logger = LoggerFactory.getLogger(InitRunner.class);

    private void setToken() {
        serverConfig.setConfigToken(TokenMaker.makeToken(serverConfig.getConfigTokenLength()));
    }
    private void makeSpace() throws IOException {
        File codeSpace = new File(serverConfig.getJudgerCodeSpacePath());
        if (!codeSpace.exists()) {
            codeSpace.mkdirs();
        }
        File dataSpace = new File(serverConfig.getJudgerDataSpacePath());
        if (!dataSpace.exists()) {
            dataSpace.mkdirs();
        }
        File workSpace = new File(serverConfig.getJudgerWorkSpace());
        if (!workSpace.exists()) {
            workSpace.mkdirs();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setToken();
        makeSpace();
        logger.info("Generate configuration request token :" + serverConfig.getConfigToken());
        logger.info("Generate http request url for configuring server : http://{this server ip}:{this server port}/Config/?token="+serverConfig.getConfigToken());
    }
}
