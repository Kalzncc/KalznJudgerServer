package com.kalzn.judger.service.judgehandle.config;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;

public class TranslatorConfig implements Serializable, Gsonable {
    static public final int COMPILER_MODE = 0;
    static public final int INTERPRETER_MODE = 1;
    static public final int COMPILER_INTERPRETER_MODE = 2;
    static public final int DO_NOT_TRANSLATE_MODE = 3;
    private Integer mode;
    private Integer translatorId;
    private String codeFileName;
    private String compilerPath;
    private String compilerInfoPath;
    private String[] compilerOptions;

    private String compilerProductName;

    private String interpreterPath;
    private String interpreterInfoPath;
    private String[] interpreterOptions;

    public String getCodeFileName() {
        return codeFileName;
    }

    public void setCodeFileName(String codeFileName) {
        this.codeFileName = codeFileName;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getCompilerPath() {
        return compilerPath;
    }

    public void setCompilerPath(String compilerPath) {
        this.compilerPath = compilerPath;
    }

    public String getCompilerInfoPath() {
        return compilerInfoPath;
    }

    public void setCompilerInfoPath(String compilerInfoPath) {
        this.compilerInfoPath = compilerInfoPath;
    }

    public String[] getCompilerOptions() {
        return compilerOptions;
    }

    public void setCompilerOptions(String[] compilerOptions) {
        this.compilerOptions = compilerOptions;
    }

    public String getCompilerProductName() {
        return compilerProductName;
    }

    public void setCompilerProductName(String compilerProductName) {
        this.compilerProductName = compilerProductName;
    }

    public String getInterpreterPath() {
        return interpreterPath;
    }

    public void setInterpreterPath(String interpreterPath) {
        this.interpreterPath = interpreterPath;
    }

    public String getInterpreterInfoPath() {
        return interpreterInfoPath;
    }

    public void setInterpreterInfoPath(String interpreterInfoPath) {
        this.interpreterInfoPath = interpreterInfoPath;
    }

    public String[] getInterpreterOptions() {
        return interpreterOptions;
    }

    public void setInterpreterOptions(String[] interpreterOptions) {
        this.interpreterOptions = interpreterOptions;
    }

    public Integer getTranslatorId() {
        return translatorId;
    }

    public void setTranslatorId(Integer translatorID) {
        this.translatorId = translatorID;
    }

    @Override
    public String toString() {
        return "TranslatorConfig{" +
                "mode=" + mode +
                ", translatorID=" + translatorId +
                ", compilerPath='" + compilerPath + '\'' +
                ", compilerInfoPath='" + compilerInfoPath + '\'' +
                ", compilerOptions=" + Arrays.toString(compilerOptions) +
                ", compilerProductName='" + compilerProductName + '\'' +
                ", interpreterPath='" + interpreterPath + '\'' +
                ", interpreterInfoPath='" + interpreterInfoPath + '\'' +
                ", interpreterOptions=" + Arrays.toString(interpreterOptions) +
                '}';
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
