package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import com.github.landyking.link.util.GUIDUtil;
import com.github.landyking.link.util.Texts;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/13.
 */
public class GUIDGenerator extends OutputOneByOneProcessor {
    public static final String SNOWFLAKE = "snowflake";
    public static final String UUID = "uuid";

    @Override
    protected void processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, ValueBag> one, String name, ValueBag item) {
        Object val = genValue(config, mojo);
        item.setModifyValue(val);
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return genValue(config, mojo);
    }

    private Object genValue(Element config, DirectiveMojo mojo) {
        String type = mojo.getParser().getParam(config, "type");
        if (type.equalsIgnoreCase(SNOWFLAKE)) {
            return GUIDUtil.nextId();
        } else if (type.equalsIgnoreCase(UUID)) {
            return Texts.uuid();
        }
        return null;
    }
}
