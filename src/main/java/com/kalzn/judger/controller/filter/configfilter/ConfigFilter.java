package com.kalzn.judger.controller.filter.configfilter;


import com.kalzn.judger.util.JudgerHttpStatusJson;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgecontrol.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;


@WebFilter(urlPatterns = "/Config/*", filterName = "configFilter")
@Order(1)
public class ConfigFilter implements Filter {

    @Autowired
    private JudgerServerConfig serverConfig;

    private Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = servletRequest.getParameter("token");
        if (token != null && token.compareTo(serverConfig.getConfigToken()) == 0) {
            if (servletRequest.getParameter("scheme") == null || servletRequest.getParameter("ip") == null || servletRequest.getParameter("port") == null || servletRequest.getParameter("server-id") == null) {
                logger.warn("Configuring Service has been requested with incomplete config (Request has been rejected) -- ip : " + servletRequest.getRemoteHost());
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            logger.warn("Configuring Service has been requested from invalid token (Request has been rejected) -- ip : " + servletRequest.getRemoteHost());
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
