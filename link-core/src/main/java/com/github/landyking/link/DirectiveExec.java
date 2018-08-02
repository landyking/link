package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Element;

import java.util.HashMap;
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
        logger.debug("执行指令: {}", mojo.getDirectiveCode());
        try {
            logger.debug("处理输入参数");
            processInputParam(mojo);
            logger.debug("执行指令内容");
            processExecution(mojo);
            logger.debug("指令执行收尾");
            processExecutionEnding(mojo);
            logger.debug("处理输出参数");
            processOutputParam(mojo);
        } catch (NullPointerException e) {
            logger.error("空指针", e);
            mojo.setException(e);
        } catch (Exception e) {
            mojo.setException(e);
        }
        logger.debug("处理结果渲染");
        processResultRender(mojo);
    }

    private void processOutputParam(DirectiveMojo mojo) throws LinkException {
        List<Map<String, Object>> outList = mojo.getEndingData().forOutput();
        //process output
        List<Element> params = mojo.getParser().getOutputParamList();
        List<Map<String, ValueBag>> finalData = Lists.newArrayListWithCapacity(outList.size());
        for (Map<String, Object> one : outList) {
            finalData.add(new HashMap<String, ValueBag>());
        }
        // 对外暴露的数据
        for (Element param : params) {
            String name = param.getAttribute("name");
            String from = param.getAttribute("from");
            if (!Texts.hasText(from)) {
                from = name;
            }
            String desc = param.getAttribute("desc");
            String fixed = null;
            boolean isFixed = param.hasAttribute("fixed");
            if (isFixed) {
                fixed = param.getAttribute("fixed");
            }
            boolean isDefault = param.hasAttribute("default");
            String defValue = null;
            if (isDefault) {
                defValue = param.getAttribute("default");
            }
            for (int i = 0; i < outList.size(); i++) {
                Map<String, ValueBag> bagMap = finalData.get(i);
                if (isFixed) {
                    bagMap.put(name, new ValueBag(false).setOriginValue(fixed));
                } else {
                    Map<String, Object> vals = outList.get(i);
                    Object o = vals.get(from);
                    if (o != null) {
                        bagMap.put(name, new ValueBag(false).setOriginValue(o));
                    } else {
                        if (isDefault) {
                            bagMap.put(name, new ValueBag(false).setOriginValue(defValue));
                        } else {
                            bagMap.put(name, new ValueBag(false).setOriginValue(null));
                        }
                    }
                }
            }
        }
        //内部使用的数据
        for (int i = 0; i < outList.size(); i++) {
            Map<String, Object> before = outList.get(i);
            Map<String, ValueBag> after = finalData.get(i);
            for (Map.Entry<String, Object> tmp : before.entrySet()) {
                if (!after.containsKey(tmp.getKey())) {
                    after.put(tmp.getKey(), new ValueBag(true).setModifyValue(tmp.getValue()));
                }
            }
        }
        for (Element param : params) {
            String name = param.getAttribute("name");
            String desc = param.getAttribute("desc");

            List<Element> processorList = mojo.getParser().getParamProcessorList(param);
            for (Element process : processorList) {
                AbstractParamProcessor pps = getParamProcessor(process);
                if (pps == null) {
                    throw new LinkException("无法获取节点" + process.getNodeName() + "对应的参数处理器");
                }
                try {
                    pps.processOutput(process, param, mojo, name, finalData);
                } catch (Exception e) {
                    throw new LinkException("处理出参[" + name + ":" + desc + "]异常", e);
                }
            }
        }
        //清理，移除内部使用的数据
        List<Map<String, ValueBag>> tmpDataList = Lists.newLinkedList();
        for (Map<String, ValueBag> one : finalData) {
            HashMap<String, ValueBag> newMap = Maps.newHashMap();
            for (Map.Entry<String, ValueBag> me : one.entrySet()) {
                if (!me.getValue().isInternal()) {
                    newMap.put(me.getKey(), me.getValue());
                }
            }
            one.clear();
            tmpDataList.add(newMap);
        }
        finalData.clear();
        mojo.setAfterOutput(tmpDataList);
    }
    @SuppressWarnings("unchecked")
    private void processExecutionEnding(DirectiveMojo mojo) throws LinkException {
        Element executionEnding = mojo.getParser().getExecutionEnding();
        String type = executionEnding.getAttribute("type");
        try {
            Class<ExecutionEnding> requiredType = (Class<ExecutionEnding>) Class.forName(type);
            ExecutionEnding bean = application.getBean(requiredType);
            ExecutionEndingData endingData = bean.process(mojo);
            mojo.setEndingData(endingData);
        } catch (ClassNotFoundException e) {
            throw new LinkException("executionEnding定义的type类" + type + "不存在");
        } catch (Exception e) {
            throw new LinkException("executionEnding处理异常", e);
        }
    }
    @SuppressWarnings("unchecked")
    private void processResultRender(DirectiveMojo mojo) throws LinkException {
        Element resultRender = mojo.getParser().getResultRender();
        String type = resultRender.getAttribute("type");
        try {
            Class<ResultRender> requiredType = (Class<ResultRender>) Class.forName(type);
            ResultRender bean = application.getBean(requiredType);
            bean.render(mojo);
        } catch (ClassNotFoundException e) {
            throw new LinkException("resultRender定义的type类" + type + "不存在");
        } catch (Exception e) {
            throw new LinkException("resultRender处理异常", e);
        }
    }

    private void processExecution(DirectiveMojo mojo) throws LinkException {
        Element execution = mojo.getParser().getExecution();
        String transaction = execution.getAttribute("transaction");
        List<AbstractExecution> execList = mojo.getParser().getExecutionList(execution);
        for (AbstractExecution one : execList) {
            one.execute(mojo);
        }
    }


    private void processInputParam(DirectiveMojo mojo) throws LinkException {
        List<Element> inputParams = mojo.getParser().getInputParamList();
        for (Element param : inputParams) {
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
            mojo.setProcessedInputParam(name, in);

        }
        for (Element param : inputParams) {
            String name = param.getAttribute("name");
            String desc = param.getAttribute("desc");
            List<Element> processorList = mojo.getParser().getParamProcessorList(param);
            for (Element process : processorList) {
                AbstractParamProcessor pps = getParamProcessor(process);
                if (pps == null) {
                    throw new LinkException("无法获取节点" + process.getNodeName() + "对应的参数处理器");
                }
                try {
                    Object val = pps.processInput(process, param, mojo, mojo.getProcessedInputParam(name));
                    mojo.setProcessedInputParam(name, val);
                } catch (LinkException e) {
                    throw e;
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
