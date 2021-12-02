package com.kalzn.judger.controller.filter.ipfilter;


import com.kalzn.judger.util.JudgerHttpStatusJson;
import com.kalzn.judger.config.JudgerServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;


@WebFilter(urlPatterns = {"/Task/*", "/Setting/*", "/Data/*"}, filterName = "nonLeaderServerIpFilter")
@Order(1)
public class NonLeaderServerIpFilter implements Filter {

    @Autowired
    private JudgerServerConfig serverConfig;

    private Logger logger = LoggerFactory.getLogger(NonLeaderServerIpFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String ip = servletRequest.getRemoteHost();
        if (serverConfig.getLeaderServerIp() == null) {
            logger.warn("Leader server is not configured (Request has been rejected) -- ip : " + ip);
            return;
        }
        if (ip.compareTo("0:0:0:0:0:0:0:1") == 0 ||  ip.compareTo(serverConfig.getLeaderServerIp()) == 0 || ip.compareTo("127.0.0.1") == 0) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            logger.warn("Task Service has been requested from invalid ip (Request has been rejected) -- ip : " + ip);
            try {
                OutputStreamWriter writer = new OutputStreamWriter(servletResponse.getOutputStream());
                writer.write(JudgerHttpStatusJson.REJECTED);
            } finally {
                servletResponse.getOutputStream().close();
            }
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
