package com.kalzn.judger.service.judgehandle.runbox;

import com.kalzn.judger.service.judgehandle.callback.Acceptable;
import com.kalzn.judger.service.judgehandle.manager.codemanager.CodeRelease;
import com.kalzn.judger.service.judgehandle.config.JudgerDataConfig;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.config.ResultConfig;
import com.kalzn.judger.service.judgehandle.config.TaskConfig;
import com.kalzn.judger.service.judgehandle.manager.datamanager.DataRequest;
import com.kalzn.judger.service.judgehandle.factory.JudgerDataSetFactory;
import com.kalzn.judger.service.judgehandle.factory.ResultConfigFactory;
import com.kalzn.judger.util.FileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component("runbox")
@Scope("prototype")
public class Runbox implements Runnable{


    private TaskConfig taskConfig;



    @Autowired
    @Qualifier("taskManager")
    private Acceptable taskManager;

    @Autowired
    private CodeRelease codeManager;


    @Autowired
    private JudgerServerConfig serverConfig;
    @Autowired
    private DataRequest dataRequest;
    @Autowired
    private JudgerDataSetFactory judgerDataSetFactory;
    @Autowired
    private ResultConfigFactory resultConfigFactory;

    public void pushTask(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public void setTaskManager(Acceptable taskManager) {
        this.taskManager = taskManager;
    }

    public void setJudgerServerConfig(JudgerServerConfig judgerServerConfig) {
        this.serverConfig = serverConfig;
    }

    public void setDataRequest(DataRequest dataRequest) {
        this.dataRequest = dataRequest;
    }

    public void setCodeManager(CodeRelease codeManager) {
        this.codeManager = codeManager;
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    private void initWorkSpace() throws IOException {
        String workSpacePath = taskConfig.getJudgerConfig().getWorkSpacePath() + "/" + String.valueOf(taskConfig.getRealTimeID());

        taskConfig.getJudgerConfig().setWorkSpacePath(workSpacePath);
        File workSpace = new File(workSpacePath);
        if (workSpace.exists()) throw new IOException("TaskID space is exists");
        workSpace.mkdirs();



        FileIO.copy(taskConfig.getJudgerConfig().getCodeSourcePath(), workSpacePath+"/"+taskConfig.getJudgerConfig().getCodeFileName());

        dataRequest.lockRead(taskConfig.getJudgerConfig().getDataID());
        FileWriter writer = null;
        try {
            String indexFilePath = dataRequest.requestData(taskConfig.getJudgerConfig().getDataID(), taskConfig.getJudgerConfig().getTranslator().getTranslatorId());
            JudgerDataConfig[] dataConfigs = judgerDataSetFactory.createJudgerDataSetFromJsonFile(indexFilePath);
            taskConfig.getJudgerConfig().setData(dataConfigs);
            String configPath = workSpacePath + "/config.json";
            writer = new FileWriter(configPath);
            writer.write(taskConfig.getJudgerConfig().toJson());
            for (JudgerDataConfig data : taskConfig.getJudgerConfig().getData()) {
                String InputDataPath = data.getInputData();
                String OutputDataPath = data.getStdAnswer();
                String sourcePath = serverConfig.getJudgerDataSpacePath() + "/" + taskConfig.getJudgerConfig().getDataID();

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
        codeManager.releaseTask(taskConfig.getRealTimeID());
        try {
            taskManager.accept(taskConfig);
        } catch (Exception e) {

        }
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
        try {
            dataRequest.report(taskConfig.getRealTimeID());
        } catch (Exception e) {
            e.printStackTrace();
            taskConfig.setTaskInfo("Can not report leader server task status " + e.getMessage());
            taskConfig.setExceptionCode(TaskConfig.CAN_NOT_CREATE_WORKSPACE);
            taskConfig.setResultConfig(null);
            commit();
            return;
        }
        Runtime runtime = Runtime.getRuntime();
        String[] argv = {taskConfig.getJudgerConfig().getWorkSpacePath()+"/config.json", serverConfig.getJudgerCoreLogPath()};
        int waitFor = 0;
        try {
            String cmd = serverConfig.getJudgerCorePath() + " " + argv[0] + " " + argv[1];
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
            return;
        }

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
        taskConfig.setExceptionCode(TaskConfig.SUCCESS);
        taskConfig.setResultConfig(resultConfig);
        taskConfig.setCompilerInfoString(compilerInfo);
        taskConfig.setInterpreterInfoString(interpreterInfo);
        commit();
    }
}
