package com.github.landyking.link.paramProcessor.custom;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/11.
 */
public class TestCustom extends AbstractParamProcessor {
    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, String in) throws Exception {
        String param2 = mojo.getParser().getParam(config, "param2");
        System.out.println("param2 " + param2);
        return null;
    }
}
