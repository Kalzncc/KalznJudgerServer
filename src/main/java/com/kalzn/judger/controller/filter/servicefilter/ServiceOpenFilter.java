package com.kalzn.judger.controller.filter.servicefilter;


import com.kalzn.judger.config.JudgerServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = {
        "/Data/ResetData"
},
        filterName = "serviceOpenFilter"
)
@Order(2)
public class ServiceOpenFilter implements Filter {
    @Autowired
    private JudgerServerConfig serverConfig;

    private Logger logger = LoggerFactory.getLogger(ServiceOpenFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (serverConfig.getServiceStatus() == JudgerServerConfig.SERVICE_ENABLE) {
            logger.warn("Reset data has requested when service is open (Request has been rejected) -- ip : " + servletRequest.getRemoteHost());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
