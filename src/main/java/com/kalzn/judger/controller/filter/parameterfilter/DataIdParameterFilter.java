package com.kalzn.judger.controller.filter.parameterfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;


@WebFilter( urlPatterns = {
            "/Setting/RegisterData",
            "/Data/UpdateData",
            "/Data/ReleaseData"
        },
        filterName = "registerDataParameterFilter"
    )
@Order(3)
public class DataIdParameterFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(DataIdParameterFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String dataId = servletRequest.getParameter("data-id");
        if (dataId == null) {
            logger.warn("Request needed data-id with invalid parameter (Request has been rejected) -- ip : " + servletRequest.getRemoteHost());
            return;
        }
        try {
            Integer.parseInt(dataId);
        } catch (NumberFormatException e) {
            logger.warn("Request needed data-id with invalid parameter (Request has been rejected) -- ip : " + servletRequest.getRemoteHost());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
