package com.kalzn.config;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;

public class JudgerConfig implements Serializable, Gsonable {
    private String taskID;
    private Integer maxCharBuffer;

    public static final int SINGLE_RESULT_MODE = 0;
    public static final int POINTS_MODE = 1;
    public static final int ONLY_COMPILE_MODE = 2;
    private Integer judgeMode;

    public static final int STD_IO = 0;
    public static final int FILE_IO = 1;
    private  Integer iOMode;

    private Integer gid;
    private Integer uid;

    public static final int NOT_STRICT_MODE = 0;
    public static final int STRICT_MODE = 1;
    private Integer strictMode;

    private String workSpacePath;
    private String resultPath;
    private String logPath;

    private Boolean sPJ;
    private String spjExePath;
    private String spjExeName;
    private Integer maxSPJTime;
    private Integer maxSPJMemory;

    private TranslatorConfig translator;
    private JudgerDataConfig[] data;

    private Integer dataID;
    private String CodeSourcePath;
    private String CodeFileName;
    private String DataSourcePath;

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public Integer getMaxCharBuffer() {
        return maxCharBuffer;
    }

    public void setMaxCharBuffer(Integer maxCharBuffer) {
        this.maxCharBuffer = maxCharBuffer;
    }

    public Integer getJudgeMode() {
        return judgeMode;
    }

    public void setJudgeMode(Integer judgeMode) {
        this.judgeMode = judgeMode;
    }

    public Integer getiOMode() {
        return iOMode;
    }

    public void setiOMode(Integer iOMode) {
        this.iOMode = iOMode;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getStrictMode() {
        return strictMode;
    }

    public void setStrictMode(Integer strictMode) {
        this.strictMode = strictMode;
    }

    public String getWorkSpacePath() {
        return workSpacePath;
    }

    public void setWorkSpacePath(String workSpacePath) {
        this.workSpacePath = workSpacePath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public Boolean getsPJ() {
        return sPJ;
    }

    public void setsPJ(Boolean sPJ) {
        this.sPJ = sPJ;
    }

    public String getSpjExePath() {
        return spjExePath;
    }

    public void setSpjExePath(String spjExePath) {
        this.spjExePath = spjExePath;
    }

    public String getSpjExeName() {
        return spjExeName;
    }

    public void setSpjExeName(String spjExeName) {
        this.spjExeName = spjExeName;
    }

    public Integer getMaxSPJTime() {
        return maxSPJTime;
    }

    public void setMaxSPJTime(Integer maxSPJTime) {
        this.maxSPJTime = maxSPJTime;
    }

    public Integer getMaxSPJMemory() {
        return maxSPJMemory;
    }

    public void setMaxSPJMemory(Integer maxSPJMemory) {
        this.maxSPJMemory = maxSPJMemory;
    }

    public TranslatorConfig getTranslator() {
        return translator;
    }

    public void setTranslator(TranslatorConfig translator) {
        this.translator = translator;
    }

    public JudgerDataConfig[] getData() {
        return data;
    }

    public void setData(JudgerDataConfig[] data) {
        this.data = data;
    }

    public String getCodeSourcePath() {
        return CodeSourcePath;
    }

    public void setCodeSourcePath(String codeSourcePath) {
        CodeSourcePath = codeSourcePath;
    }

    public String getDataSourcePath() {
        return DataSourcePath;
    }

    public void setDataSourcePath(String dataSourcePath) {
        DataSourcePath = dataSourcePath;
    }

    public String getCodeFileName() {
        return CodeFileName;
    }

    public void setCodeFileName(String codeFileName) {
        CodeFileName = codeFileName;
    }

    public Integer getDataID() {
        return dataID;
    }

    public void setDataID(Integer dataID) {
        this.dataID = dataID;
    }

    @Override
    public String toString() {
        return "JudgerConfig{" +
                "taskID='" + taskID + '\'' +
                ", maxCharBuffer=" + maxCharBuffer +
                ", judgeMode=" + judgeMode +
                ", iOMode=" + iOMode +
                ", gid=" + gid +
                ", uid=" + uid +
                ", strictMode=" + strictMode +
                ", workSpacePath='" + workSpacePath + '\'' +
                ", resultPath='" + resultPath + '\'' +
                ", logPath='" + logPath + '\'' +
                ", sPJ=" + sPJ +
                ", spjExePath='" + spjExePath + '\'' +
                ", spjExeName='" + spjExeName + '\'' +
                ", maxSPJTime=" + maxSPJTime +
                ", maxSPJMemory=" + maxSPJMemory +
                ", translator=" + translator +
                ", data=" + Arrays.toString(data) +
                ", dataID=" + dataID +
                ", CodeSourcePath='" + CodeSourcePath + '\'' +
                ", CodeFileName='" + CodeFileName + '\'' +
                ", DataSourcePath='" + DataSourcePath + '\'' +
                '}';
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
