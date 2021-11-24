package com.kalzn.runbox;

import com.google.gson.Gson;
import com.kalzn.callback.Acceptable;
import com.kalzn.config.JudgerDataConfig;
import com.kalzn.config.JudgerServerConfig;
import com.kalzn.config.ResultConfig;
import com.kalzn.config.TaskConfig;
import com.kalzn.datamanager.inter.DataRequest;
import com.kalzn.factory.JudgerDataSetFactory;
import com.kalzn.factory.ResultConfigFactory;
import com.kalzn.util.FileIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Runbox implements Runnable{
    private TaskConfig taskConfig;
    private Acceptable manager;
    private JudgerServerConfig judgerServerConfig;
    private DataRequest dataRequest;
    public Runbox(TaskConfig taskConfig, Acceptable manager, JudgerServerConfig judgerServerConfig, DataRequest dataRequest) {
        this.taskConfig = taskConfig;
        this.manager = manager;
        this.judgerServerConfig = judgerServerConfig;
        this.dataRequest = dataRequest;
    }
    private void initWorkSpace() throws IOException {
        String workSpacePath = taskConfig.getJudgerConfig().getWorkSpacePath() + "/" + String.valueOf(taskConfig.getRealTimeID());

        taskConfig.getJudgerConfig().setWorkSpacePath(workSpacePath);
        File workSpace = new File(workSpacePath);
        if (!workSpace.mkdirs()) {
            throw new IOException("Work space can not be built");
        }



        FileIO.copy(taskConfig.getJudgerConfig().getCodeSourcePath(), workSpacePath+"/"+taskConfig.getJudgerConfig().getCodeFileName());

        dataRequest.lockRead(taskConfig.getJudgerConfig().getDataID());
        FileWriter writer = null;
        try {
            String indexFilePath = dataRequest.requestData(taskConfig.getJudgerConfig().getDataID(), taskConfig.getJudgerConfig().getTranslator().getTranslatorID());
            JudgerDataSetFactory judgerDataSetFactory = new JudgerDataSetFactory(judgerServerConfig);
            JudgerDataConfig[] dataConfigs = judgerDataSetFactory.createJudgerDataSetFromJsonFile(indexFilePath);
            taskConfig.getJudgerConfig().setData(dataConfigs);
            String configPath = workSpacePath + "/config.json";
            writer = new FileWriter(configPath);
            writer.write(taskConfig.getJudgerConfig().toJson());
            for (JudgerDataConfig data : taskConfig.getJudgerConfig().getData()) {
                String InputDataPath = data.getInputData();
                String OutputDataPath = data.getStdAnswer();
                String sourcePath = taskConfig.getJudgerConfig().getDataSourcePath();

                FileIO.copy(sourcePath+"/"+InputDataPath, workSpacePath+"/"+InputDataPath);
                FileIO.copy(sourcePath+"/"+OutputDataPath, workSpacePath+"/"+OutputDataPath);
            }
        } finally {
            if (writer != null) writer.close();
            dataRequest.unlockRead(taskConfig.getJudgerConfig().getDataID());
        }


    }
    private void commit() {
        FileIO.delete(taskConfig.getJudgerConfig().getWorkSpacePath());
        manager.accept(taskConfig);
    }
    @Override
    public void run() {
        try {
            initWorkSpace();
        } catch (Exception e) {
            e.printStackTrace();
            taskConfig.setTaskInfo("Work space can not be built " + e.getMessage());
            taskConfig.setExceptionCode(TaskConfig.CAN_NOT_CREATE_WORKSPACE);
            taskConfig.setResultConfig(null);
            commit();
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        String[] argv = {taskConfig.getJudgerConfig().getWorkSpacePath()+"/config.json", judgerServerConfig.getJudgerCoreLogPath()};
        int waitFor = 0;
        try {
            String cmd = judgerServerConfig.getJudgerCorePath() + " " + argv[0] + " " + argv[1];
            Process ps = runtime.exec(cmd);
            waitFor = ps.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            taskConfig.setTaskInfo("Judger core can not be loaded " + e.getMessage());
            taskConfig.setExceptionCode(TaskConfig.CAN_NOT_LOAD_JUDGER_CORE);
            taskConfig.setResultConfig(null);
            commit();
            e.printStackTrace();
            return;
        }
        if (waitFor != 0) {
            taskConfig.setTaskInfo("Judger core error");
            taskConfig.setExceptionCode(TaskConfig.JUDGER_CORE_ERROR);
        }

        ResultConfigFactory resultConfigFactory = new ResultConfigFactory(judgerServerConfig);
        ResultConfig resultConfig = null;
        String compilerInfo = null;
        String interpreterInfo = null;
        try {
            resultConfig = resultConfigFactory.createResultConfigFromJsonFile(taskConfig.getJudgerConfig().getWorkSpacePath() + "/" + taskConfig.getJudgerConfig().getResultPath());
        } catch (Exception e) {
            e.printStackTrace();
            taskConfig.setTaskInfo("Result info can not be collected " + e.getMessage());
            taskConfig.setExceptionCode(TaskConfig.CAN_NOT_CREATE_WORKSPACE);
            taskConfig.setResultConfig(null);
            commit();
            return;
        }
        try {
            compilerInfo = FileIO.readWholeStringFile(taskConfig.getJudgerConfig().getWorkSpacePath() + "/" + taskConfig.getJudgerConfig().getTranslator().getCompilerInfoPath());

        } catch (Exception e) {
            compilerInfo = "";
        }
        try {
            interpreterInfo = FileIO.readWholeStringFile(taskConfig.getJudgerConfig().getWorkSpacePath() + "/" + taskConfig.getJudgerConfig().getTranslator().getInterpreterInfoPath());
        } catch (Exception e) {
            interpreterInfo = "";
        }
        taskConfig.setResultConfig(resultConfig);
        taskConfig.setCompilerInfoString(compilerInfo);
        taskConfig.setInterpreterInfoString(interpreterInfo);
        commit();
    }
}
