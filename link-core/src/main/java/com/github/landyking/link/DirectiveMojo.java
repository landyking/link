package com.github.landyking.link;

/**
 * Created by landy on 2018/7/5.
 * 指令上下文
 */
public class DirectiveMojo {
    private final DirectiveParser parser;
    private final InputPot pot;
    private final String code;

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
}
