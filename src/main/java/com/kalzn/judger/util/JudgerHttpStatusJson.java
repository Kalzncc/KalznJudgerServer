package com.kalzn.judger.util;

public class JudgerHttpStatusJson {

    private final static String prefix = "{\"rseult\":\"";
    private final static String suffix = "\"}";

    public final static String OK = prefix+0+suffix;
    public final static String REJECTED  = prefix+1+suffix;
    public final static String FAILURE = prefix+2+suffix;

    public static String getFailureJson(String info) {
        return prefix + 2 + "\",\"info\":\"" + info + suffix;
    }
    public static String getOkJson(String info) {
        return prefix + 0 + "\",\"info\":\"" + info + suffix;
    }
    public static String getRejectedJson(String info) {
        return prefix + 1 + "\",\"info\":" + info + suffix;
    }

}
