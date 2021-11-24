package com.kalzn.config;

import java.io.Serializable;
import java.util.Map;

public class JudgerServerConfig implements Serializable, Gsonable {
    private Integer serverID;
    private Integer maxJsonFileSize;

    private String judgerCorePath;
    private String judgerCoreLogPath;
    private String judgerWorkSpace;
    private String judgerDataSpacePath;
    private String judgerCodeSpacePath;

    private String leaderServerUrl;
    private Integer leaderServerTimeout;
    private Integer DataDownloadBufferSize;

    private Integer maxSPJTime;
    private Integer maxSPJMemory;
    private Integer maxBufferChar;

    private Integer maxTaskRunningSize;
    private Integer maxTaskWaitingSize;
    private Integer minActiveTaskThreadPoolNumber;
    private Integer TaskThreadActiveTime;

    public static final int SUBMIT = 0;
    public static final int CALLED = 1;
    private Integer ResultAcceptWay;

    public Integer getTaskThreadActiveTime() {
        return TaskThreadActiveTime;
    }

    public void setTaskThreadActiveTime(Integer taskThreadActiveTime) {
        TaskThreadActiveTime = taskThreadActiveTime;
    }

    private Map<Integer, TranslatorConfig> translators;


    public String getJudgerDataSpacePath() {
        return judgerDataSpacePath;
    }

    public void setJudgerDataSpacePath(String judgerDataSpacePath) {
        this.judgerDataSpacePath = judgerDataSpacePath;
    }

    public Integer getMaxJsonFileSize() {
        return maxJsonFileSize;
    }

    public void setMaxJsonFileSize(Integer maxJsonFileSize) {
        this.maxJsonFileSize = maxJsonFileSize;
    }

    public String getJudgerCorePath() {
        return judgerCorePath;
    }

    public void setJudgerCorePath(String judgerCorePath) {
        this.judgerCorePath = judgerCorePath;
    }

    public String getJudgerCoreLogPath() {
        return judgerCoreLogPath;
    }

    public void setJudgerCoreLogPath(String judgerCoreLogPath) {
        this.judgerCoreLogPath = judgerCoreLogPath;
    }

    public String getJudgerWorkSpace() {
        return judgerWorkSpace;
    }

    public void setJudgerWorkSpace(String judgerWorkSpace) {
        this.judgerWorkSpace = judgerWorkSpace;
    }

    public String getJudgerCodeSpacePath() {
        return judgerCodeSpacePath;
    }

    public void setJudgerCodeSpacePath(String judgerCodeSpacePath) {
        this.judgerCodeSpacePath = judgerCodeSpacePath;
    }

    public Integer getServerID() {
        return serverID;
    }

    public void setServerID(Integer serverID) {
        this.serverID = serverID;
    }

    public String getLeaderServerUrl() {
        return leaderServerUrl;
    }

    public void setLeaderServerUrl(String leaderServerUrl) {
        leaderServerUrl = leaderServerUrl;
    }

    public Integer getLeaderServerTimeout() {
        return leaderServerTimeout;
    }

    public void setLeaderServerTimeout(Integer leaderServerTimeout) {
        leaderServerTimeout = leaderServerTimeout;
    }

    public Integer getDataDownloadBufferSize() {
        return DataDownloadBufferSize;
    }

    public void setDataDownloadBufferSize(Integer dataDownloadBufferSize) {
        DataDownloadBufferSize = dataDownloadBufferSize;
    }

    public Map<Integer, TranslatorConfig> getTranslators() {
        return translators;
    }

    public void setTranslators(Map<Integer, TranslatorConfig> translators) {
        this.translators = translators;
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

    public Integer getMaxBufferChar() {
        return maxBufferChar;
    }

    public void setMaxBufferChar(Integer maxBufferChar) {
        this.maxBufferChar = maxBufferChar;
    }

    public Integer getMaxTaskRunningSize() {
        return maxTaskRunningSize;
    }

    public void setMaxTaskRunningSize(Integer maxTaskRunningSize) {
        this.maxTaskRunningSize = maxTaskRunningSize;
    }

    public Integer getMaxTaskWaitingSize() {
        return maxTaskWaitingSize;
    }

    public void setMaxTaskWaitingSize(Integer maxTaskWaitingSize) {
        this.maxTaskWaitingSize = maxTaskWaitingSize;
    }

    public Integer getMinActiveTaskThreadPoolNumber() {
        return minActiveTaskThreadPoolNumber;
    }

    public void setMinActiveTaskThreadPoolNumber(Integer minActiveTaskThreadPoolNumber) {
        this.minActiveTaskThreadPoolNumber = minActiveTaskThreadPoolNumber;
    }

    public Integer getResultAcceptWay() {
        return ResultAcceptWay;
    }

    public void setResultAcceptWay(Integer resultAcceptWay) {
        ResultAcceptWay = resultAcceptWay;
    }

    @Override
    public String toJson() {
        return null;
    }
}
