package com.kalzn.judger.controller.filter.servicefilter;

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


@WebFilter(urlPatterns = {
            "/Task/*",
            "/Data/*"
        },
        filterName = "serviceCloseFilter"
    )
@Order(2)
public class ServiceCloseFilter implements Filter {

    @Autowired
    private JudgerServerConfig serverConfig;

    private Logger logger = LoggerFactory.getLogger(ServiceCloseFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (serverConfig.getServiceStatus() == JudgerServerConfig.SERVICE_NOT_ENABLE) {
            logger.warn("Task or Data Service has been request when service is close (Request has been rejected) -- ip : " + servletRequest.getRemoteHost());
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
