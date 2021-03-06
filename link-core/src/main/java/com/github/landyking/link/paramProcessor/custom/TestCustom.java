package com.github.landyking.link.paramProcessor.custom;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/11.
 */
public class TestCustom extends AbstractParamProcessor {
    @Override
    public void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, Object>> outList) {

    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        String param2 = mojo.getParser().getParamText(config, "param2");
        System.out.println("param2 " + param2);
        return null;
    }
}
