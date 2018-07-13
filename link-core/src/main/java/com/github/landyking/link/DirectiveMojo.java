package com.github.landyking.link;

import com.google.common.collect.Maps;

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

    public String getCode() {
        return code;
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
}
