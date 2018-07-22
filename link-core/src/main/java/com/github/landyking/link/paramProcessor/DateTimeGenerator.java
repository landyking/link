package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import com.github.landyking.link.util.Texts;
import org.joda.time.DateTime;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author: landy
 * @date: 2018-07-22 22:05
 */
public class DateTimeGenerator extends OutputOneByOneProcessor {
    public static final String TYPE_LONG = "long";
    private static final String TYPE_STRING = "string";
    private static final String TYPE_DATE = "date";

    @Override
    protected void processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, ValueBag> one, String name, ValueBag item) {
        Object val = process(mojo, config);
        item.setModifyValue(val);
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return process(mojo, config);
    }

    private Object process(DirectiveMojo mojo, Element config) {

        String type = Texts.firstHasText(mojo.getParser().getParam(config, "type"), TYPE_LONG);
        if (Texts.isSame(TYPE_LONG, type)) {
            return System.currentTimeMillis();
        } else if (Texts.isSame(TYPE_STRING, type)) {
            String fmt = Texts.firstHasText(mojo.getParser().getParam(config, "format"), "yyyyMMddHHmmss");
            return DateTime.now().toString(fmt);
        } else if (Texts.isSame(TYPE_DATE, type)) {
            return DateTime.now().toDate();
        }
        return null;
    }
}
