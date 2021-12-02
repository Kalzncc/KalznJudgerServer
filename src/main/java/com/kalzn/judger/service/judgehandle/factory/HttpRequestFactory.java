package com.kalzn.judger.service.judgehandle.factory;

import com.kalzn.judger.config.JudgerServerConfig;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("httpRequestFactory")
public class HttpRequestFactory {
    @Autowired
    private JudgerServerConfig serverConfig;

    public GetMethod createGetMethod() {
        GetMethod getMethod = new GetMethod();
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, serverConfig.getLeaderServerTimeout());
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        return getMethod;
    }
    public PostMethod createPostMethod() {
        PostMethod postMethod = new PostMethod();
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, serverConfig.getLeaderServerTimeout());
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        return postMethod;
    }
}
