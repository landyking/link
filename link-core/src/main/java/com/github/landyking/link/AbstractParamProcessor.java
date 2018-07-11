package com.github.landyking.link;

import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/11.
 */
public abstract class AbstractParamProcessor {
    public String tag() {
        return getClass().getSimpleName();
    }

    public abstract Object processInput(Element config, Element param, DirectiveMojo mojo, String in) throws Exception;
}
