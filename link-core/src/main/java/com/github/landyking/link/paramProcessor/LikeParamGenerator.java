package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
import org.joda.time.DateTime;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author: landy
 * @date: 2018-07-22 22:05
 */
public class LikeParamGenerator extends OutputOneByOneProcessor {
    @Override
    protected void processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, ValueBag> one, String name, ValueBag item) throws LinkException {
        /*Object val = process(mojo, config);
        item.setModifyValue(val);*/
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return process(mojo, config, in);
    }

    private Object process(DirectiveMojo mojo, Element config, Object in) throws LinkException {
        if (in == null || !Texts.hasText(in)) {
            return in;
        }
        Object position = mojo.getParser().getParam(config, "position");
        if ("left".equals(position)) {
            return "%" + in;
        } else if ("right".equals(position)) {
            return in + "%";
        } else if ("both".equals(position)) {
            return "%" + in + "%";
        }
        throw new LinkException("未知的position: " + position);
    }
}
