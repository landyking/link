package com.github.landyking.link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Created by landy on 2018/7/5.
 * 指令执行器
 */
public class DirectiveExec implements BeanFactoryAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private BeanFactory beanFactory;

    public void execute(DirectiveMojo mojo) {
        logger.info("execute directive: {}", mojo.getDirectiveCode());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}