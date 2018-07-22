package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/11.
 */
public class AgeCalc extends AbstractParamProcessor {

    @Override
    public void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, ValueBag>> outList) {

    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return null;
    }
}
