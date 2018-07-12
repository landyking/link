package com.github.landyking.link.execution;

import com.github.landyking.link.AbstractExecution;
import com.github.landyking.link.AbstractExecutionFactory;
import com.github.landyking.link.exception.LinkException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/12.
 */
public class CustomExecution implements AbstractExecutionFactory, ApplicationContextAware {
    private ApplicationContext application;

    @Override
    public String tag() {
        return "custom";
    }

    @Override
    public AbstractExecution generate(Element element) throws LinkException {
        String className = element.getAttribute("className");
        try {
            Class<?> clazz = Class.forName(className);
            AbstractExecution rst = (AbstractExecution) clazz.getConstructor(Element.class).newInstance(element);
            application.getAutowireCapableBeanFactory().autowireBean(rst);
            return rst;
        } catch (Exception e) {
            throw new LinkException("自定义执行器类" + className + "实例化异常", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application = applicationContext;
    }
}
