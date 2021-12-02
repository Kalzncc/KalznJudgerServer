package com.kalzn.judger.controller.filter.parameterfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;


@WebFilter(urlPatterns = "/Task/Submit", filterName = "submitParameterFilter")
@Order(3)
public class SubmitParameterFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(SubmitParameterFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest.getParameter("config-str") == null || servletRequest.getParameter("code") == null) {
            logger.warn("Task submit service has been requested with incomplete config (Request has been rejected) -- ip : " + servletRequest.getRemoteHost());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
