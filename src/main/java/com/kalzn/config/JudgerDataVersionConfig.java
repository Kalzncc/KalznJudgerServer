package com.kalzn.config;

import com.google.gson.Gson;

import java.io.Serializable;

public class JudgerDataVersionConfig implements Serializable, Gsonable {
    private int dataID;
    private int serverID;
    private int thisServerVersion;
    private int LeaderServerVersion;

    public int getDataID() {
        return dataID;
    }

    public void setDataID(int dataID) {
        this.dataID = dataID;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public int getThisServerVersion() {
        return thisServerVersion;
    }

    public void setThisServerVersion(int thisServerVersion) {
        this.thisServerVersion = thisServerVersion;
    }

    public int getLeaderServerVersion() {
        return LeaderServerVersion;
    }

    public void setLeaderServerVersion(int leaderServerVersion) {
        LeaderServerVersion = leaderServerVersion;
    }

    @Override
    public String toJson() {
        return (new Gson()).toJson(this);
    }
}
