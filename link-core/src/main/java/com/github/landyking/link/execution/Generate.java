package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelTool;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/8/15.
 */
public class Generate implements AbstractExecutionFactory, ApplicationContextAware {
    private ApplicationContext application;

    @Override
    public String tag() {
        return "generate";
    }

    @Override
    public AbstractExecution generate(Element element) throws LinkException {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
                String id = element.getAttribute("id");
                List<Element> varList = mojo.getParser().getSubElementList(element, "lk:var");
                Map<String, Object> rst = Maps.newHashMap();
                for (Element one : varList) {
                    String name = one.getAttribute("name");
                    String from = one.getAttribute("from");
                    Object value = null;
                    if (Texts.hasText(from)) {
                        SpelPair sp = SpelUtils.getSpelPair(mojo);
                        value = sp.getExp().parseExpression(from).getValue(sp.getCtx());
                    }
                    rst.put(name, value);
                    List<Element> processorList = mojo.getParser().getParamProcessorList(one);
                    for (Element process : processorList) {
                        AbstractParamProcessor pps = getParamProcessor(process);
                        if (pps == null) {
                            throw new LinkException("无法获取节点" + process.getNodeName() + "对应的参数处理器");
                        }
                        try {
                            Object val = pps.processInput(process, one, mojo, rst.get(name));
                            rst.put(name, val);
                        } catch (LinkException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new LinkException("处理节点" + mojo.getParser().getFullPath(process, false) + "异常", e);
                        }
                    }
                }
                ExecuteResult er = new ExecuteResult();
                er.setData(Collections.singletonList(rst));
                mojo.setExecuteResult(id, er);
            }
        };
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
