package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.util.Texts;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by landy on 2018/7/11.
 */
public class AllToString extends OutputOneByOneProcessor {

    @Override
    protected Object processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, Object> one, String name, Object item) {
        return Texts.toStr(item);
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return Texts.toStr(in);
    }
}
