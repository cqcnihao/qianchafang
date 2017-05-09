package com.qianchafang.framework.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Poan on 2017/5/9.
 * 非web环境下获取spring组件
 */
@Service
public class BeanFactory {
    private static volatile ApplicationContext applicationContext;

    private static volatile boolean initByBeanFactory = false;

    private static final Log logger = LogFactory.getLog(BeanFactory.class);

    @Autowired
    public void setApplicationContext(ApplicationContext ac) {
        logger.info("init bean factory via setter.");
        if (applicationContext != null && applicationContext != ac) {
            logger.info("found exist applicationContext while setting, old one:"
                    + applicationContext + ", new one:" + ac);
        }
        applicationContext = ac;
    }

    public static final void init() {
        if (applicationContext == null) {
            synchronized (BeanFactory.class) {
                if (applicationContext == null) {
                    logger.warn("init bean factory self.");
                    initByBeanFactory = true;
                    applicationContext = new ClassPathXmlApplicationContext(
                            "classpath*:spring/*.xml");
                }
            }
        }
    }

    public static final <T> T getBean(Class<T> clazz) {
        init();
        return applicationContext.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static final <T> T getBean(String beanName) {
        init();
        return (T) applicationContext.getBean(beanName);
    }

    public static final <T> Map<String, T> getBeansOfType(Class<T> type) {
        init();
        return applicationContext.getBeansOfType(type);
    }

    /**
     * @return 是否被beanFactory初始化
     */
    public static final boolean initByBeanFactory() {
        return initByBeanFactory;
    }

    /**
     * @return spring是否被初始化
     */
    public static final boolean inited() {
        return applicationContext != null;
    }

}
