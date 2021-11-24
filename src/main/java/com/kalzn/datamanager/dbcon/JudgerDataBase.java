package com.kalzn.datamanager.dbcon;


import com.google.gson.Gson;
import com.kalzn.config.JudgerDataVersionConfig;
import com.kalzn.config.JudgerServerConfig;
import com.kalzn.factory.JudgerDataSetFactory;
import com.kalzn.util.FileIO;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;
import java.util.Map;

/**
 *  This is not a real database,
 *  This is a virtual database that can get data from leader server, leader server return data.
 */
public class JudgerDataBase {
    JudgerServerConfig config;




    public JudgerDataBase(JudgerServerConfig config) {
        this.config =config;
    }

    private void updateDataVersion(int dataID) throws IOException {
        HttpClient leaderHttpClient = new HttpClient();
        leaderHttpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getLeaderServerTimeout());
        GetMethod getMethod = new GetMethod(config.getLeaderServerUrl() + "/UpdateDataVersion");
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, config.getLeaderServerTimeout());
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        getMethod.getParams().setParameter("dataID", dataID);
        getMethod.getParams().setParameter("serverID", config.getServerID());
        try {
            int statusCode = leaderHttpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
            }
        } finally {
            getMethod.releaseConnection();
        }
    }
    private void releaseDataVersion(int dataID) throws IOException {
        HttpClient leaderHttpClient = new HttpClient();
        leaderHttpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getLeaderServerTimeout());
        GetMethod getMethod = new GetMethod(config.getLeaderServerUrl() + "/ReleaseDataVersion");
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, config.getLeaderServerTimeout());
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        getMethod.getParams().setParameter("dataID", dataID);
        getMethod.getParams().setParameter("serverID", config.getServerID());
        try {
            int statusCode = leaderHttpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
            }
        } finally {
            getMethod.releaseConnection();
        }
    }

    public boolean checkDataEnable(int dataID) throws IOException {
        HttpClient leaderHttpClient = new HttpClient();
        leaderHttpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getLeaderServerTimeout());
        GetMethod getMethod = new GetMethod(config.getLeaderServerUrl() + "/DataCheck");
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, config.getLeaderServerTimeout());
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        getMethod.getParams().setParameter("serverID", config.getServerID());
        getMethod.getParams().setParameter("dataID", dataID);
        String response = "";
        try {
            int statusCode = leaderHttpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
            }
            InputStream inputStream = getMethod.getResponseBodyAsStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String temp = null;
            while( (temp = bufferedReader.readLine()) != null) {
                stringBuffer.append(temp+"\n");
            }
            response = stringBuffer.toString();
        } finally {
            getMethod.releaseConnection();
        }
        JudgerDataVersionConfig versionConfig = (new Gson()).fromJson(response, JudgerDataVersionConfig.class);
        if (versionConfig.getThisServerVersion() != versionConfig.getLeaderServerVersion())
            return false;
        return true;
    }

    public void updateData(int dataID) throws IOException{
        String dir = config.getJudgerDataSpacePath() + "/" + dataID;
        HttpClient leaderHttpClient = new HttpClient();
        leaderHttpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getLeaderServerTimeout());
        GetMethod getMethod = new GetMethod(config.getLeaderServerUrl() + "/DataDownLoad");
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, config.getLeaderServerTimeout());
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        getMethod.getParams().setParameter("dataID", dataID);

        FileOutputStream fileOutputStream = null;
        try {
            int statusCode = leaderHttpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
            }
            InputStream inputStream = getMethod.getResponseBodyAsStream();
            (new File(dir)).mkdirs();
            fileOutputStream = new FileOutputStream(new File(dir + "/data.zip"));

            byte[] buffer = new byte[config.getDataDownloadBufferSize()];
            int ch;
            while( (ch = inputStream.read(buffer))!=-1) {
                fileOutputStream.write(buffer, 0, ch);
            }
            FileIO.unzip(dir+"/data.zip", dir, config.getDataDownloadBufferSize());
            FileIO.delete(dir+"/data.zip");
            updateDataVersion(dataID);
        } catch (IOException e) {
            if (new File(dir).exists()) FileIO.delete(dir);
            throw e;
        } finally {
            if (fileOutputStream != null) fileOutputStream.close();
            getMethod.releaseConnection();
        }
    }
    public void releaseData(int dataID) throws IOException{
        releaseDataVersion(dataID);
        String dir = config.getJudgerDataSpacePath() + "/" + dataID;
        if (new File(dir).exists()) {
            FileIO.delete(dir);
        }
    }

    public Integer[] pushAllEnableDataID() throws IOException {
        HttpClient leaderHttpClient = new HttpClient();
        leaderHttpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getLeaderServerTimeout());
        GetMethod getMethod = new GetMethod(config.getLeaderServerUrl() + "/AllEnableDataID");
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, config.getLeaderServerTimeout());
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = leaderHttpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
            }
            InputStream inputStream = getMethod.getResponseBodyAsStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String temp = null;
            while( (temp = bufferedReader.readLine()) != null) {
                stringBuffer.append(temp+"\n");
            }
            response = stringBuffer.toString();
        } finally {
            getMethod.releaseConnection();
        }
        return (new Gson()).fromJson(response, Integer[].class);
    }
}
