package com.kalzn.config;

import com.google.gson.Gson;

import java.io.Serializable;

public class JudgerDataConfig implements Serializable, Gsonable {



    private String inputData;
    private String outputData;
    private String stdAnswer;
    private Integer maxCPUTime;
    private Integer maxMemory;
    private Integer maxStack;

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }

    public String getStdAnswer() {
        return stdAnswer;
    }

    public void setStdAnswer(String stdAnswer) {
        this.stdAnswer = stdAnswer;
    }

    public Integer getMaxCPUTime() {
        return maxCPUTime;
    }

    public void setMaxCPUTime(Integer maxCPUTime) {
        this.maxCPUTime = maxCPUTime;
    }

    public Integer getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(Integer maxMemory) {
        this.maxMemory = maxMemory;
    }

    public Integer getMaxStack() {
        return maxStack;
    }

    public void setMaxStack(Integer maxStack) {
        this.maxStack = maxStack;
    }

    @Override
    public String toString() {
        return "Data{" +
                "inputData='" + inputData + '\'' +
                ", outputData='" + outputData + '\'' +
                ", stdAnswer='" + stdAnswer + '\'' +
                ", maxCPUTime=" + maxCPUTime +
                ", maxMemory=" + maxMemory +
                ", maxStack=" + maxStack +
                '}';
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
