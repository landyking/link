package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Element;

import java.util.*;

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
        SpelPair spelPair = SpelUtils.getSpelPair(mojo);
        Element opnode = mojo.getParser().getOutputNode();
        Object rst = processOutputSubNode(mojo, mojo.getParser().reverseOutputChildList(opnode).iterator().next(), spelPair);
        mojo.setAfterOutput(rst);
    }

    @SuppressWarnings("unchecked")
    private Object processOutputSubNode(DirectiveMojo mojo, Element opnode, SpelPair spelPair) throws LinkException {
        if (opnode.getNodeName().equals("map")) {
            String from = opnode.getAttribute("from");
            if (Texts.hasText(from)) {
                return spelPair.getExp().parseExpression(from).getValue(spelPair.getCtx());//fixme 原始值，不是ValueBag
            } else {
                Map<String, Object> rst = Maps.newHashMap();
                List<Element> list = mojo.getParser().nodeList2ElementList(opnode.getChildNodes());
                for (Element e : list) {
                    rst.put(e.getAttribute("name"), processOutputSubNode(mojo, e, spelPair));
                }
                return rst;
            }
        } else if (opnode.getNodeName().equals("list")) {
            String from = opnode.getAttribute("from");
            if (Texts.hasText(from)) {
                return spelPair.getExp().parseExpression(from).getValue(spelPair.getCtx());//fixme 原始值，不是ValueBag
            } else {
                Map<String, Collection<Object>> childs = Maps.newHashMap();
                List<Element> list = mojo.getParser().nodeList2ElementList(opnode.getChildNodes());
                for (Element e : list) {
                    Object subVal = processOutputSubNode(mojo, e, spelPair);
                    String name = e.getAttribute("name");
                    if (subVal == null) {
                        childs.put(name, Collections.emptyList());
                    } else if (subVal instanceof Collection) {
                        childs.put(name, (Collection<Object>) subVal);
                    } else {
                        String path = mojo.getParser().getFullPath(e, true);
                        throw new LinkException(path + "的表达式求值结果不是列表");
                    }
                }
                //检查长度是否一致
                int len = checkLengthEqual(mojo, childs, opnode);
                //抽取map<list>中的数据重新组装成list<map>
                List<Map<String, Object>> rst = Lists.newArrayListWithCapacity(len);
                for (int i = 0; i < len; i++) {
                    rst.add(Maps.<String, Object>newHashMap());
                }
                for (Map.Entry<String, Collection<Object>> et : childs.entrySet()) {
                    String key = et.getKey();
                    Collection<Object> value = et.getValue();
                    int i = 0;
                    for (Object o : value) {
                        rst.get(i++).put(key, o);
                    }
                }
                return rst;
            }
        } else if (opnode.getNodeName().equals("param")) {
            return processNodeParam(mojo, opnode, spelPair);
        } else {
            throw new LinkException("不支持的结点" + mojo.getParser().getFullPath(opnode, true));
        }
    }

    private Object processNodeParam(DirectiveMojo mojo, Element param, SpelPair spelPair) throws LinkException {
        String name = param.getAttribute("name");
        String desc = param.getAttribute("desc");
        String from = param.getAttribute("from");
        if (!Texts.hasText(from)) {
            String pnode = param.getParentNode().getNodeName();
            if (pnode.equals("list")) {
                from = "[exec][default].data.![#this[" + name + "]]";
            } else if (pnode.equals("map")) {
                from = "[exec][default].data[0][" + name + "]";
            }
        }
        List<Map<String, Object>> valueBagList = Lists.newArrayList();
        Object value = null;
        try {
            value = spelPair.getExp().parseExpression(from).getValue(spelPair.getCtx());
        } catch (Exception e) {
            String fullPath = mojo.getParser().getFullPath(param, true);
            throw new LinkException("节点" + fullPath + "的from表达式" + from + "处理异常", e);
        }
        boolean single;
        if (value instanceof Collection) {
            single = false;
            for (Object o : ((Collection) value)) {
                HashMap<String, Object> map = Maps.newHashMap();
                map.put(name, o);
                valueBagList.add(map);
            }
        } else {
            single = true;
            HashMap<String, Object> map = Maps.newHashMap();
            map.put(name, value);
            valueBagList.add(map);
        }

        List<Element> processorList = mojo.getParser().getParamProcessorList(param);
        for (Element process : processorList) {
            AbstractParamProcessor pps = getParamProcessor(process);
            if (pps == null) {
                throw new LinkException("无法获取节点" + process.getNodeName() + "对应的参数处理器");
            }
            try {
                pps.processOutput(process, param, mojo, name, valueBagList);
            } catch (Exception e) {
                throw new LinkException("处理出参[" + name + ":" + desc + "]异常", e);
            }
        }
        if (single) {
            return valueBagList.iterator().next().get(name);
        } else {
            List<Object> list = Lists.newArrayListWithCapacity(valueBagList.size());
            for (Map<String, Object> one : valueBagList) {
                list.add(one.get(name));
            }
            return list;
        }
    }

    /**
     * 检查map下面每个list的长度。保证所有长度一致
     *
     * @param mojo
     * @param childs
     * @param opnode
     * @throws LinkException
     */
    private int checkLengthEqual(DirectiveMojo mojo, Map<String, Collection<Object>> childs, Element opnode) throws LinkException {
        Map<String, Integer> keySize = Maps.newHashMap();
        for (Map.Entry<String, Collection<Object>> one : childs.entrySet()) {
            keySize.put(one.getKey(), one.getValue().size());
        }
        Collection<Integer> values = keySize.values();
        boolean pass = true;
        int tmp = -1;
        for (Integer one : values) {
            if (tmp == -1) {
                tmp = one;
            } else {
                pass = tmp == one;
                if (!pass) {
                    break;
                }
            }
        }
        if (!pass) {
            String fullPath = mojo.getParser().getFullPath(opnode, true);
            throw new LinkException(fullPath + "下属结点值列表长度不一致:" + keySize.toString());
        }
        return tmp;
    }

    @SuppressWarnings("unchecked")
    private void processExecutionEnding(DirectiveMojo mojo) throws LinkException {
        Element executionEnding = mojo.getParser().getExecutionEnding();
        String type = executionEnding.getAttribute("type");
        try {
            Class<ExecutionEnding> requiredType = (Class<ExecutionEnding>) Class.forName(type);
            ExecutionEnding bean = application.getBean(requiredType);
            Map<String, ExecuteResult> endingData = bean.process(mojo);
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

    private void processExecution(final DirectiveMojo mojo) throws LinkException {
        final Element execution = mojo.getParser().getExecution();
        String transaction = execution.getAttribute("transactionDataSource");
        if (Texts.hasText(transaction)) {
            DataSourceManager dsm = application.getBean(DataSourceManager.class);
            TransactionTemplate ttp = dsm.getTransactionTemplate(transaction, null);
            ttp.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        processExecutionList(mojo, execution);
                    } catch (LinkException e) {
                        Throwables.propagate(e);
                    }
                }
            });
        } else {
            processExecutionList(mojo, execution);
        }
    }

    private void processExecutionList(DirectiveMojo mojo, Element execution) throws LinkException {
        List<AbstractExecution> execList = mojo.getParser().getExecutionList(execution);
        for (AbstractExecution one : execList) {
            String test = one.getElement().getAttribute("test");
            if (Texts.hasText(test)) {
                SpelPair spelPair = SpelUtils.getSpelPair(mojo);
                Boolean needExec = spelPair.getExp().parseExpression(test).getValue(spelPair.getCtx(), Boolean.class);
                if (needExec) {
                    one.execute(mojo);
                } else {
                    String id = one.getElement().getAttribute("id");
                    mojo.getExecuteResultMap().put(id, new ExecuteResult());
                }
            } else {
                one.execute(mojo);
            }
        }
    }


    private void processInputParam(DirectiveMojo mojo) throws LinkException {
        List<Element> inputParams = mojo.getParser().getInputParamList();
        for (Element param : inputParams) {
            String in;
            String name = param.getAttribute("name");
            String desc = param.getAttribute("desc");
            String notEmpty = param.getAttribute("notEmpty");
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
