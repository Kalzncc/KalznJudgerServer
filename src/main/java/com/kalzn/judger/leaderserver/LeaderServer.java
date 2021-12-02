package com.kalzn.judger.leaderserver;

import com.google.gson.Gson;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.callback.Acceptable;
import com.kalzn.judger.service.judgehandle.config.JudgerDataVersionConfig;
import com.kalzn.judger.service.judgehandle.config.TaskConfig;
import com.kalzn.judger.util.FileIO;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.io.*;


@Repository("leaderServer")
public class LeaderServer implements Acceptable {
    @Autowired
    private JudgerServerConfig serverConfig;
    @Autowired
    private ApplicationContext applicationContext;

    public void setServerConfig(JudgerServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void executeRequest(PostMethod postMethod) throws IOException {
        HttpClient leaderHttpClient = (HttpClient) applicationContext.getBean("httpClient");
        int statusCode = leaderHttpClient.executeMethod(postMethod);
        if (statusCode != HttpStatus.SC_OK) {
            throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
        }
    }
    private PostMethod redirectPostMethod(String url) throws URIException {
        PostMethod postMethod = (PostMethod) applicationContext.getBean("postMethod");
        URI uri = new HttpURL(url);
        postMethod.setURI(uri);
        return postMethod;
    }


    private void executeRequest(GetMethod getMethod) throws IOException {
        HttpClient leaderHttpClient = (HttpClient) applicationContext.getBean("httpClient");
        int statusCode = leaderHttpClient.executeMethod(getMethod);
        if (statusCode != HttpStatus.SC_OK) {
            throw new HttpException("Request Leader Server Error (Status code :" + statusCode + ")");
        }
    }
    private void downloadFile(GetMethod getMethod, String dir) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            InputStream inputStream = getMethod.getResponseBodyAsStream();
            FileIO.delete(dir);
            (new File(dir)).mkdirs();
            fileOutputStream = new FileOutputStream(new File(dir + "/data.zip"));

            byte[] buffer = new byte[serverConfig.getDataDownloadBufferSize()];
            int ch;
            while ((ch = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, ch);
            }

            FileIO.unzip(dir + "/data.zip", dir, serverConfig.getDataDownloadBufferSize());
            FileIO.delete(dir + "/data.zip");
        } finally {
            if (fileOutputStream!=null) fileOutputStream.close();
        }
    }
    private String getResponseBodyString(GetMethod getMethod) throws IOException {

        InputStream inputStream = getMethod.getResponseBodyAsStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        while( (temp = bufferedReader.readLine()) != null) {
            stringBuffer.append(temp+"\n");
        }
        return stringBuffer.toString();
    }
    private GetMethod redirectGetMethod(String url) throws URIException {
        GetMethod getMethod = (GetMethod) applicationContext.getBean("getMethod");
        URI uri = new HttpURL(url);
        getMethod.setURI(uri);
        return getMethod;
    }

    private void updateDataVersion(int dataID) throws IOException {
        GetMethod getMethod = redirectGetMethod(serverConfig.getLeaderServerUrl() + "/UpdateDataVersion?serverID="+serverConfig.getServerId()+"&dataID="+dataID);
        try {
            executeRequest(getMethod);
        } finally {
            getMethod.releaseConnection();
        }
    }
    private void releaseDataVersion(int dataID) throws IOException {
        GetMethod getMethod = redirectGetMethod(serverConfig.getLeaderServerUrl() + "/ReleaseDataVersion?serverID="+serverConfig.getServerId()+"&dataID="+dataID);
        try {
            executeRequest(getMethod);
        } finally {
            getMethod.releaseConnection();
        }
    }

    public boolean checkDataEnable(int dataID) throws IOException {
        GetMethod getMethod = redirectGetMethod(serverConfig.getLeaderServerUrl() + "/DataCheck?serverID="+serverConfig.getServerId()+"&dataID="+dataID);
        String response = "";
        try {
            executeRequest(getMethod);
            response = getResponseBodyString(getMethod);
        } finally {
            getMethod.releaseConnection();
        }
        JudgerDataVersionConfig versionConfig = (new Gson()).fromJson(response, JudgerDataVersionConfig.class);
        if (versionConfig.getThisServerVersion() != versionConfig.getLeaderServerVersion())
            return false;
        return true;
    }

    public void updateData(int dataID) throws IOException{
        String dir = serverConfig.getJudgerDataSpacePath() + "/" + dataID;
        GetMethod getMethod = redirectGetMethod(serverConfig.getLeaderServerUrl() + "/DataDownLoad?dataID="+dataID);
        try {
            executeRequest(getMethod);
            downloadFile(getMethod, dir);
            updateDataVersion(dataID);
        } catch (IOException e) {
            if (new File(dir).exists()) FileIO.delete(dir);
            throw e;
        } finally {
            getMethod.releaseConnection();
        }
    }
    public long releaseData(int dataID) throws IOException{
        releaseDataVersion(dataID);
        String dir = serverConfig.getJudgerDataSpacePath() + "/" + dataID;
        if (new File(dir).exists()) {
            return FileIO.delete(dir);
        }
        return (long)0;
    }
    public void reportTask(String taskID) throws IOException {
        GetMethod getMethod = redirectGetMethod(serverConfig.getLeaderServerUrl() + "/ReportTask?taskID="+taskID+"&serverID="+serverConfig.getServerId());
        try {
            executeRequest(getMethod);
        } finally {
            getMethod.releaseConnection();
        }
    }
    public Integer[] pushAllEnableDataID() throws IOException {
        GetMethod getMethod = redirectGetMethod(serverConfig.getLeaderServerUrl() + "/AllEnableDataID");
        String response = "";
        try {
            executeRequest(getMethod);
            response = getResponseBodyString(getMethod);
        } finally {
            getMethod.releaseConnection();
        }
        return (new Gson()).fromJson(response, Integer[].class);
    }

    @Override
    public void accept(TaskConfig taskConfig) throws IOException{
        NameValuePair[] parma = {new NameValuePair("result", taskConfig.toJson())};
        PostMethod postMethod = redirectPostMethod(serverConfig.getLeaderServerUrl()+"/ReportResult");
        postMethod.setRequestBody(parma);
        try {
            executeRequest(postMethod);
        } finally {
            postMethod.releaseConnection();
        }
    }
}
