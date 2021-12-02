package com.kalzn.judger.service.judgecontrol.impl;

import com.google.gson.Gson;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.manager.codemanager.CodeHandle;
import com.kalzn.judger.service.judgecontrol.TaskService;
import com.kalzn.judger.service.judgehandle.manager.taskmanager.TaskHandle;
import com.kalzn.judger.service.judgehandle.config.TaskRequestConfig;
import com.kalzn.judger.service.judgehandle.manager.datamanager.DataHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service("judgerService")
public class TaskServiceImpl implements TaskService {


    @Autowired
    private JudgerServerConfig serverConfig;
    @Autowired
    private TaskHandle taskManager;
    @Autowired
    private DataHandle dataManager;
    @Autowired
    private CodeHandle codeManager;







    @Override
    public void submit(String requestStr, String code) throws IOException, InterruptedException {
        TaskRequestConfig task = (new Gson()).fromJson(requestStr, TaskRequestConfig.class);
        codeManager.saveCode(task.getTaskID(), task.getCompilerID(), code);
        taskManager.submit(task);
    }

    public void setCodeManager(CodeHandle codeManager) {
        this.codeManager = codeManager;
    }

    public JudgerServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public TaskHandle getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskHandle taskManager) {
        this.taskManager = taskManager;
    }

    public DataHandle getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataHandle dataManager) {
        this.dataManager = dataManager;
    }


}
