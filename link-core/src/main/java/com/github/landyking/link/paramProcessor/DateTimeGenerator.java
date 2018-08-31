package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import org.joda.time.DateTime;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author: landy
 * @date: 2018-07-22 22:05
 */
public class DateTimeGenerator extends OutputOneByOneProcessor {
    @Override
    protected Object processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, Object> one, String name, Object item) {
        Object val = process(mojo, config);
        return val;
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return process(mojo, config);
    }

    private Object process(DirectiveMojo mojo, Element config) {
        return DateTime.now();
    }
}
