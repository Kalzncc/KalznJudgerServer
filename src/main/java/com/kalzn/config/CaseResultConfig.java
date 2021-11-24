package com.kalzn.config;

import com.google.gson.Gson;

import java.io.Serializable;

public class CaseResultConfig implements Serializable, Gsonable {
    private Integer time;
    private Integer realTime;
    private Integer memory;
    private Integer signal;
    private Integer code;

    public static final int ACCEPTED = 0;
    public static final int WRONG_ANSWER = 1;
    public static final int TIME_LIMIT_EXCEEDED = 2;
    public static final int MEMORY_LIMIT_EXCEEDED = 3;
    public static final int RUNTIME_ERROR = 4;
    public static final int PRESENTATION_ERROR = 5;
    public static final int OUTPUT_LIMIT_EXCEEDED = 6;
    public static final int COMPILE_ERROR = 7;
    public static final int SKIP = 8;
    public static final int SYSTEM_ERROR = 9;
    private Integer result;
    private String detail;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getRealTime() {
        return realTime;
    }

    public void setRealTime(Integer realTime) {
        this.realTime = realTime;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Integer getSignal() {
        return signal;
    }

    public void setSignal(Integer signal) {
        this.signal = signal;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "CaseResultConfig{" +
                "time=" + time +
                ", realTime=" + realTime +
                ", memory=" + memory +
                ", signal=" + signal +
                ", code=" + code +
                ", result=" + result +
                ", detail='" + detail + '\'' +
                '}';
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
