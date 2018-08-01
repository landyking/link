package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/11.
 */
public class CustomParamProcessor extends AbstractParamProcessor implements ApplicationContextAware {
    private ApplicationContext application;

    @Override
    public void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, ValueBag>> outList) throws Exception {
        AbstractParamProcessor pps = getAbstractParamProcessor(config);
        pps.processOutput(config, param, mojo, name, outList);
    }

    @Override
    public String tag() {
        return "Custom";
    }

    @Override

    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        AbstractParamProcessor pps = getAbstractParamProcessor(config);
        return pps.processInput(config, param, mojo, in);
    }

    private AbstractParamProcessor getAbstractParamProcessor(Element config) throws ClassNotFoundException {
        String className = config.getAttribute("className");
        Assert.hasText(className, "自定义参数处理器类不能为空");
        Class<AbstractParamProcessor> requiredType = (Class<AbstractParamProcessor>) Class.forName(className);
        return application.getBean(requiredType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application = applicationContext;
    }
}
