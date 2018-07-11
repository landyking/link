package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/5.
 * 指令执行器
 */
public class DirectiveExec implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext application;

    public void execute(DirectiveMojo mojo) throws LinkException {
        logger.info("执行指令: {}", mojo.getDirectiveCode());
        logger.info("处理输入参数");
        processInputParam(mojo);
        logger.info("执行指令内容");
        processExecution(mojo);
        logger.info("处理输出参数");
    }

    private void processExecution(DirectiveMojo mojo) throws LinkException {
        Element execution = mojo.getParser().getExecution();
        String transaction = execution.getAttribute("transaction");
        List<Element> execList = mojo.getParser().getExecutionElementList(execution);
        for (Element one : execList) {
            AbstractExecution e = getExecution(one);
            if (e == null) {
                throw new LinkException("无法获取节点" + one.getNodeName() + "对应的执行器");
            }
            e.execute(one, mojo);
        }
    }

    private AbstractExecution getExecution(Element e) {
        Map<String, AbstractExecution> beans = application.getBeansOfType(AbstractExecution.class);
        for (AbstractExecution one : beans.values()) {
            if (one.tag().equalsIgnoreCase(e.getNodeName())) {
                return one;
            }
        }
        return null;
    }


    private void processInputParam(DirectiveMojo mojo) throws LinkException {
        List<Element> inputParams = mojo.getParser().getInputParamList();
        for (Element param : inputParams) {
            Object val = null;
            String in;
            String name = param.getAttribute("name");
            String desc = param.getAttribute("desc");
            String notEmpty = param.getAttribute("notEmpty");
            if (param.hasAttribute("fixed")) {
                in = param.getAttribute("fixed");
            } else {
                in = mojo.getPot().getInputParamText(name);
                if (!Texts.hasText(in)) {
                    //参数为空，进行检测
                    if (param.hasAttribute("default")) {
                        in = param.getAttribute("default");
                    }
                    if (LkTools.isTrue(notEmpty) && !Texts.hasText(in)) {
                        //不能为空，但实际为空，抛出异常
                        throw new LinkException("参数[" + name + ":" + desc + "]不能为空");
                    }
                }
            }
            List<Element> processorList = mojo.getParser().getParamProcessorList(param);
            for (Element process : processorList) {
                AbstractParamProcessor pps = getParamProcessor(process);
                if (pps == null) {
                    throw new LinkException("无法获取节点" + process.getNodeName() + "对应的参数处理器");
                }
                try {
                    val = pps.processInput(process, param, mojo, in);
                } catch (Exception e) {
                    throw new LinkException("处理入参[" + name + ":" + desc + "]异常", e);
                }
            }
        }

    }

    private AbstractParamProcessor getParamProcessor(Element process) {
        Map<String, AbstractParamProcessor> beans = application.getBeansOfType(AbstractParamProcessor.class);
        for (AbstractParamProcessor one : beans.values()) {
            if (one.tag().equalsIgnoreCase(process.getNodeName())) {
                return one;
            }
        }
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application = applicationContext;
    }
}
