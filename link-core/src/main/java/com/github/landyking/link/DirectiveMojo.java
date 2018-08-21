package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/5.
 * 指令上下文
 */
public class DirectiveMojo {
    private final DirectiveParser parser;
    private final InputPot pot;
    private final String code;
    private final Map<String, Object> processedInputParam = Maps.newHashMap();

    private final transient Map<String, ExecuteResult> executeResultMap = Maps.newLinkedHashMap();
    private final LocalDictManager localDictManager;
    /**
     * execution执行结束，然后对执行参数的数据进行处理后得到的数据
     */
    private transient Map<String, ExecuteResult> endingData;
    /**
     * 指令执行过程中参数的异常，不包括渲染
     */
    private Exception exception;
    /**
     * 经过output节点处理之后的数据
     */
    private Object afterOutput;
    private final ApplicationContext applicationContext;

    public DirectiveMojo(String code, InputPot pot, DirectiveParser parser, ApplicationContext applicationContext) {
        this.code = code;
        this.pot = pot;
        this.parser = parser;
        this.parser.setDirectiveMojo(this);
        this.applicationContext = applicationContext;
        this.localDictManager = applicationContext.getBean(LocalDictManager.class);
    }

    public DirectiveParser getParser() {
        return parser;
    }

    public InputPot getPot() {
        return pot;
    }


    public String getDirectiveCode() {
        return this.code;
    }

    public Map<String, Object> getProcessedInputParamMap() {
        return processedInputParam;
    }

    public Object getProcessedInputParam(String name) {
        return processedInputParam.get(name);
    }

    public void setProcessedInputParam(String name, Object in) {
        processedInputParam.put(name, in);
    }

    public void setExecuteResult(String executionId, ExecuteResult rst) {
        Assert.isTrue(!executeResultMap.containsKey(executionId), executionId + "的执行结果已存在");
        executeResultMap.put(executionId, rst);
    }

    public Map<String, ExecuteResult> getExecuteResultMap() {
        return executeResultMap;
    }

    public void setEndingData(Map<String, ExecuteResult> endingData) {
        this.endingData = endingData;
    }

    public Map<String, ExecuteResult> getEndingData() {
        return endingData;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public Object getAfterOutput() {
        return afterOutput;
    }

    public void setAfterOutput(Object afterOutput) {
        this.afterOutput = afterOutput;
    }

    public LocalDict localDict(String name) {
        return localDictManager.getLocalDict(name);
    }

    public LocalDictItem localDictItem(String name) throws LinkException {
        String[] arr = name.split("@");
        Assert.isTrue(arr.length == 2, "表达式需要使用@号分隔");
        LocalDict localDict = localDictManager.getLocalDict(arr[0]);
        Assert.notNull(localDict, "本地字典" + arr[0] + "不存在");
        for (LocalDictItem item : localDict.getItems().values()) {
            if (arr[1].equals(item.getMarker())) {
                return item;
            }
        }
        throw new LinkException("字典[" + localDict.getName() + ":" + localDict.getDesc() + "]不包含marker为[" + arr[1] + "]的字典项");
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void publishEvent(String name) {
        applicationContext.publishEvent(new MojoSpringEvent(name));
    }

    public String localDictItemCode(String name) throws LinkException {
        LocalDictItem item = localDictItem(name);
        return item.getCode();
    }

}
