package com.kalzn.judger.config;

import com.kalzn.judger.service.judgehandle.config.Gsonable;
import com.kalzn.judger.service.judgehandle.config.TranslatorConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;


@Configuration
@ImportResource("classpath:application-bean.xml")
@ConfigurationProperties(prefix = "judger", ignoreInvalidFields = true)
@Component("serverConfig")
public class JudgerServerConfig implements Serializable, Gsonable {
    private Integer serverId;
    private Integer maxJsonFileSize = 0x7ffffff0; // B

    private String judgerCorePath;
    private String judgerCoreLogPath;
    private String judgerWorkSpace;
    private String judgerDataSpacePath;
    private String judgerCodeSpacePath;

    private String leaderServerUrl;

    private String leaderServerIp;
    private String configToken;
    private Integer configTokenLength;

    private Integer leaderServerTimeout = 10000; // ms
    private Integer DataDownloadBufferSize = 1024; // B

    private Integer maxSpjTime = 30000; // ms
    private Integer maxSpjMemory = 1024000;  // KB
    private Integer maxBufferChar = 0x7ffffff0; // B

    private Integer maxTaskRunningSize = 10;
    private Integer maxTaskWaitingSize = 1024;
    private Integer minActiveTaskThreadPoolNumber = 5;
    private Integer taskThreadActiveTime = 100; // ms

    public static final int SUBMIT = 0;
    public static final int CALLED = 1;
    private Integer resultAcceptWay = 0;

    private Integer dataSpaceMaxSize=  20480; // MB
    private Integer dataSpaceClearSize = 10240; // MB
    private Integer dataSpaceSizeCheckTime = 30; // minute

    public static final int SERVICE_ENABLE = 1;
    public static final int SERVICE_NOT_ENABLE = 0;
    private Integer serviceStatus = 0;

    private Map<Integer, TranslatorConfig> translators;


    public Integer getTaskThreadActiveTime() {
        return taskThreadActiveTime;
    }



    public void setTaskThreadActiveTime(Integer taskThreadActiveTime) {
        this.taskThreadActiveTime = taskThreadActiveTime;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getLeaderServerIp() {
        return leaderServerIp;
    }

    public void setLeaderServerIp(String leaderServerIp) {
        this.leaderServerIp = leaderServerIp;
    }

    public Integer getConfigTokenLength() {
        return configTokenLength;
    }

    public void setConfigTokenLength(Integer configTokenLength) {
        this.configTokenLength = configTokenLength;
    }

    public String getConfigToken() {
        return configToken;
    }

    public void setConfigToken(String configToken) {
        this.configToken = configToken;
    }

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

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getLeaderServerUrl() {
        return leaderServerUrl;
    }

    public void setLeaderServerUrl(String leaderServerUrl) {
        this.leaderServerUrl = leaderServerUrl;
    }

    public Integer getLeaderServerTimeout() {
        return leaderServerTimeout;
    }

    public void setLeaderServerTimeout(Integer leaderServerTimeout) {
        this.leaderServerTimeout = leaderServerTimeout;
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

    public Integer getMaxSpjTime() {
        return maxSpjTime;
    }

    public void setMaxSpjTime(Integer maxSPJTime) {
        this.maxSpjTime = maxSPJTime;
    }

    public Integer getMaxSpjMemory() {
        return maxSpjMemory;
    }

    public void setMaxSpjMemory(Integer maxSPJMemory) {
        this.maxSpjMemory = maxSPJMemory;
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
        return resultAcceptWay;
    }

    public void setResultAcceptWay(Integer resultAcceptWay) {
        this.resultAcceptWay = resultAcceptWay;
    }


    public Integer getDataSpaceMaxSize() {
        return dataSpaceMaxSize;
    }


    public void setDataSpaceMaxSize(Integer dataSpaceMaxSize) {
        this.dataSpaceMaxSize = dataSpaceMaxSize;
    }


    public Integer getDataSpaceClearSize() {
        return dataSpaceClearSize;
    }


    public void setDataSpaceClearSize(Integer dataSpaceClearSize) {
        this.dataSpaceClearSize = dataSpaceClearSize;
    }


    public Integer getDataSpaceSizeCheckTime() {
        return dataSpaceSizeCheckTime;
    }


    public void setDataSpaceSizeCheckTime(Integer dataSpaceSizeCheckTime) {
        this.dataSpaceSizeCheckTime = dataSpaceSizeCheckTime;
    }

    @Override
    public String toJson() {
        return  "serverId=" + serverId +
                ", maxJsonFileSize=" + maxJsonFileSize +
                ", judgerCorePath='" + judgerCorePath + '\'' +
                ", judgerCoreLogPath='" + judgerCoreLogPath + '\'' +
                ", judgerWorkSpace='" + judgerWorkSpace + '\'' +
                ", judgerDataSpacePath='" + judgerDataSpacePath + '\'' +
                ", judgerCodeSpacePath='" + judgerCodeSpacePath + '\'' +
                ", leaderServerTimeout=" + leaderServerTimeout +
                ", DataDownloadBufferSize=" + DataDownloadBufferSize +
                ", maxSpjTime=" + maxSpjTime +
                ", maxSpjMemory=" + maxSpjMemory +
                ", maxBufferChar=" + maxBufferChar +
                ", maxTaskRunningSize=" + maxTaskRunningSize +
                ", maxTaskWaitingSize=" + maxTaskWaitingSize +
                ", minActiveTaskThreadPoolNumber=" + minActiveTaskThreadPoolNumber +
                ", taskThreadActiveTime=" + taskThreadActiveTime +
                ", dataSpaceMaxSize=" + dataSpaceMaxSize +
                ", dataSpaceClearSize=" + dataSpaceClearSize +
                ", dataSpaceSizeCheckTime=" + dataSpaceSizeCheckTime +
                ", serviceStatus=" + serviceStatus +
                ", translators=" + translators;
    }

    @Override
    public String toString() {
        return "JudgerServerConfig{" +
                "serverId=" + serverId +
                ", maxJsonFileSize=" + maxJsonFileSize +
                ", judgerCorePath='" + judgerCorePath + '\'' +
                ", judgerCoreLogPath='" + judgerCoreLogPath + '\'' +
                ", judgerWorkSpace='" + judgerWorkSpace + '\'' +
                ", judgerDataSpacePath='" + judgerDataSpacePath + '\'' +
                ", judgerCodeSpacePath='" + judgerCodeSpacePath + '\'' +
                ", leaderServerUrl='" + leaderServerUrl + '\'' +
                ", leaderServerIp='" + leaderServerIp + '\'' +
                ", configToken='" + configToken + '\'' +
                ", configTokenLength=" + configTokenLength +
                ", leaderServerTimeout=" + leaderServerTimeout +
                ", DataDownloadBufferSize=" + DataDownloadBufferSize +
                ", maxSpjTime=" + maxSpjTime +
                ", maxSpjMemory=" + maxSpjMemory +
                ", maxBufferChar=" + maxBufferChar +
                ", maxTaskRunningSize=" + maxTaskRunningSize +
                ", maxTaskWaitingSize=" + maxTaskWaitingSize +
                ", minActiveTaskThreadPoolNumber=" + minActiveTaskThreadPoolNumber +
                ", taskThreadActiveTime=" + taskThreadActiveTime +
                ", resultAcceptWay=" + resultAcceptWay +
                ", dataSpaceMaxSize=" + dataSpaceMaxSize +
                ", dataSpaceClearSize=" + dataSpaceClearSize +
                ", dataSpaceSizeCheckTime=" + dataSpaceSizeCheckTime +
                ", serviceStatus=" + serviceStatus +
                ", translators=" + translators +
                '}';
    }
}
