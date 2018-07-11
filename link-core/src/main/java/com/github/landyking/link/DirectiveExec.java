package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;

/**
 * Created by landy on 2018/7/5.
 * 指令执行器
 */
public class DirectiveExec implements BeanFactoryAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private BeanFactory beanFactory;

    public void execute(DirectiveMojo mojo) throws LinkException {
        logger.info("执行指令: {}", mojo.getDirectiveCode());
        logger.info("处理输入参数");
        processInputParam(mojo);
        logger.info("执行指令内容");
        processExecution(mojo);
        logger.info("处理输出参数");
    }

    private void processExecution(DirectiveMojo mojo)throws LinkException {
        Element execution= mojo.getParser().getExecution();
        String transaction = execution.getAttribute("transaction");
        List<Element> execList= mojo.getParser().getExecutionElementList(execution);
        for (Element one : execList) {
            System.out.println(one.getNodeName());
        }
    }


    private void processInputParam(DirectiveMojo mojo) throws LinkException {
        List<Element> inputParams = mojo.getParser().getInputParamList();
        for (Element param : inputParams) {
            String name = param.getAttribute("name");
            String desc = param.getAttribute("desc");
            String notEmpty = param.getAttribute("notEmpty");
            String defval = param.getAttribute("default");
            String fixed = param.getAttribute("fixed");
            System.out.println(Arrays.asList(name,desc,notEmpty,defval,fixed));
            List<Element> processorList = mojo.getParser().getParamProcessorList(param);
            for (Element process : processorList) {
                System.out.println("# "+process.getNodeName());
            }
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
