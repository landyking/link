package com.github.landyking.link;

import com.google.common.collect.Maps;
import org.springframework.util.Assert;

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

    private final transient Map<String, ExecuteResult> executeResultMap = Maps.newHashMap();
    /**
     * execution执行结束，然后对执行参数的数据进行处理后得到的数据
     */
    private transient ExecutionEndingData endingData;
    /**
     * 指令执行过程中参数的异常，不包括渲染
     */
    private Exception exception;
    /**
     * 经过output节点处理之后的数据
     */
    private Object afterOutput;

    public DirectiveMojo(String code, InputPot pot, DirectiveParser parser) {
        this.code = code;
        this.pot = pot;
        this.parser = parser;
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

    public void setEndingData(ExecutionEndingData endingData) {
        this.endingData = endingData;
    }

    public ExecutionEndingData getEndingData() {
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
}
