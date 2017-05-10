package com.qianchafang.user.passport.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Poan on 2017/5/10.
 */
public class QcfRequestHolder extends RequestContextHolder {
    private static final String USER_ID = "__CURRENT_LOGINED_USERID__";

    private static final String ACCESS_LOCATION = "__ACCESS_LOCATION__";

    private static final String ACCESS_TIMESTAMP = "__ACCESS_TIMESTAMP__";

    private static final String USER_AGENT = "__USER_AGENT__";

    private static Logger logger = LoggerFactory.getLogger(QcfRequestHolder.class);

    public static int getUserId() {
        try {
            Integer userId = (Integer) currentRequestAttributes().getAttribute(USER_ID,
                    RequestAttributes.SCOPE_REQUEST);
            if (userId == null) {
                return 0;
            } else {
                return userId;
            }
        } catch (IllegalStateException e) {
            logger.error("fail to get user id from current request, RequestURI:{}",
                    getRequest() == null ? "can't get RequestURI"
                            : getRequest().getRequestURI()/*, e*/);
            // 在非www环境使用
            return 0;
        }
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) currentRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    public static void setUserId(int userId) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
    }

    public static final Long getRequestTimestamp() {
        try {
            Long channel = (Long) currentRequestAttributes().getAttribute(ACCESS_TIMESTAMP,
                    RequestAttributes.SCOPE_REQUEST);
            return channel;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setRequestTimestamp(Long timestamp) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(ACCESS_TIMESTAMP, timestamp, RequestAttributes.SCOPE_REQUEST);
    }

    public static final String getUserAgent() {
        try {
            String r = (String) currentRequestAttributes().getAttribute(USER_AGENT,
                    RequestAttributes.SCOPE_REQUEST);
            return r;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setUserAgent(String param) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(USER_AGENT, param, RequestAttributes.SCOPE_REQUEST);
    }
}
