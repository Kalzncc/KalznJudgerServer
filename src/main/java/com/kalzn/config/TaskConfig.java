package com.kalzn.config;

import java.io.Serializable;

public class TaskConfig implements Serializable {
    private String realTimeID;
    private JudgerConfig judgerConfig;
    private ResultConfig resultConfig;
    private String logString;
    private String compilerInfoString;
    private String interpreterInfoString;
    private String TaskInfo;

    public static final int SUCCESS = 0;
    public static final int JUDGER_CORE_ERROR = 1;
    public static final int CAN_NOT_CREATE_WORKSPACE = 2;
    public static final int CAN_NOT_LOAD_JUDGER_CORE = 3;
    private Integer ExceptionCode;

    public TaskConfig(String realTimeID, JudgerConfig judgerConfig) {
        this.realTimeID = realTimeID;
        this.judgerConfig = judgerConfig;
        logString = "No info.";
        compilerInfoString = "No info.";
        interpreterInfoString = "No info.";
        TaskInfo = "No info.";
        resultConfig = null;
    }

    public String getRealTimeID() {
        return realTimeID;
    }

    public void setRealTimeID(String realTimeID) {
        this.realTimeID = realTimeID;
    }

    public JudgerConfig getJudgerConfig() {
        return judgerConfig;
    }

    public void setJudgerConfig(JudgerConfig judgerConfig) {
        this.judgerConfig = judgerConfig;
    }

    public ResultConfig getResultConfig() {
        return resultConfig;
    }

    public void setResultConfig(ResultConfig resultConfig) {
        this.resultConfig = resultConfig;
    }

    public String getLogString() {
        return logString;
    }

    public void setLogString(String logString) {
        this.logString = logString;
    }

    public String getCompilerInfoString() {
        return compilerInfoString;
    }

    public void setCompilerInfoString(String compilerInfoString) {
        this.compilerInfoString = compilerInfoString;
    }

    public String getInterpreterInfoString() {
        return interpreterInfoString;
    }

    public void setInterpreterInfoString(String interpreterInfoString) {
        this.interpreterInfoString = interpreterInfoString;
    }

    public String getTaskInfo() {
        return TaskInfo;
    }

    public void setTaskInfo(String taskInfo) {
        TaskInfo = taskInfo;
    }

    public Integer getExceptionCode() {
        return ExceptionCode;
    }

    public void setExceptionCode(Integer exceptionCode) {
        ExceptionCode = exceptionCode;
    }

    @Override
    public String toString() {
        return "TaskConfig{" +
                "realTimeID=" + realTimeID +
                ", judgerConfig=" + judgerConfig +
                ", resultConfig=" + resultConfig +
                ", logString='" + logString + '\'' +
                ", compilerInfoString='" + compilerInfoString + '\'' +
                ", interpreterInfoString='" + interpreterInfoString + '\'' +
                ", TaskInfo='" + TaskInfo + '\'' +
                ", ExceptionCode=" + ExceptionCode +
                '}';
    }
}
