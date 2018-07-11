package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/11.
 */
public class CustomParamProcessor extends AbstractParamProcessor implements ApplicationContextAware {
    private ApplicationContext application;

    @Override
    public String tag() {
        return "Custom";
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, String in) throws Exception {
        String className = config.getAttribute("className");
        Assert.hasText(className, "自定义参数处理器类不能为空");
        Object pps = application.getBean(Class.forName(className));
        return ((AbstractParamProcessor) pps).processInput(config, param, mojo, in);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application = applicationContext;
    }
}
