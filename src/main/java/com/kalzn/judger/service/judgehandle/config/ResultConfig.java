package com.kalzn.judger.service.judgehandle.config;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;

public class ResultConfig implements Serializable, Gsonable {
    private String taskID;
    private String doneTime;
    private Integer judgeTime;
    private Integer extraTime;
    private CaseResultConfig[] result;

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }

    public Integer getJudgeTime() {
        return judgeTime;
    }

    public void setJudgeTime(Integer judgeTime) {
        this.judgeTime = judgeTime;
    }

    public Integer getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(Integer extraTime) {
        this.extraTime = extraTime;
    }

    public CaseResultConfig[] getResult() {
        return result;
    }

    public void setResult(CaseResultConfig[] result) {
        this.result = result;
    }
    @Override
    public String toString() {
        return "ResultConfig{" +
                "taskID='" + taskID + '\'' +
                ", doneTime='" + doneTime + '\'' +
                ", judgeTime=" + judgeTime +
                ", extraTime=" + extraTime +
                ", result=" + Arrays.toString(result) +
                '}';
    }
    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
