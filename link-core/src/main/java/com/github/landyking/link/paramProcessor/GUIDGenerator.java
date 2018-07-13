package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.util.GUIDUtil;
import com.github.landyking.link.util.Texts;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/13.
 */
public class GUIDGenerator extends AbstractParamProcessor {
    public static final String SNOWFLAKE = "snowflake";
    public static final String UUID = "uuid";

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        String type = mojo.getParser().getParam(config, "type");
        if (type.equalsIgnoreCase(SNOWFLAKE)) {
            return GUIDUtil.nextId();
        } else if (type.equalsIgnoreCase(UUID)) {
            return Texts.uuid();
        }
        return null;
    }
}