package com.kalzn.config;

import com.google.gson.Gson;

import java.io.Serializable;

public class TaskRequestConfig implements Serializable, Gsonable {

    private String taskID;
    private String codePath;
    private Integer compilerID;
    private Integer dataID;
    private boolean sPJ;
    private int judgeMode;
    private int strictMode;
    private int iOMode;

    public String getCodePath() {
        return codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public Integer getCompilerID() {
        return compilerID;
    }

    public void setCompilerID(Integer compilerID) {
        this.compilerID = compilerID;
    }

    public Integer getDataID() {
        return dataID;
    }

    public void setDataID(Integer dataID) {
        this.dataID = dataID;
    }

    public boolean issPJ() {
        return sPJ;
    }

    public void setsPJ(boolean sPJ) {
        this.sPJ = sPJ;
    }

    public int getJudgeMode() {
        return judgeMode;
    }

    public void setJudgeMode(int judgeMode) {
        this.judgeMode = judgeMode;
    }

    public int getStrictMode() {
        return strictMode;
    }

    public void setStrictMode(int strictMode) {
        this.strictMode = strictMode;
    }

    public int getiOMode() {
        return iOMode;
    }

    public void setiOMode(int iOMode) {
        this.iOMode = iOMode;
    }

    public String  getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    @Override
    public String toJson() {
        return (new Gson()).toJson(this);
    }
}
