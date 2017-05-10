package com.qianchafang.framework.web.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Poan on 2017/5/10.
 */
public class TicketInterceptor extends HandlerInterceptorAdapter {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    /**
     * 检测用户的ticket
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // todo：如果没有ticket或是过期 ，不让访问资源
        return false;
    }

}
