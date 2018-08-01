package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.DateTimeTool;
import com.github.landyking.link.util.Texts;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/13.
 */
public class DateTimeConverter extends OutputOneByOneProcessor {

    @Override
    protected void processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, ValueBag> one, String name, ValueBag item) throws LinkException {
        Object val = genValue(config, mojo, item.getFinalValue());
        item.setModifyValue(val);
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object obj) throws Exception {
        return genValue(config, mojo, obj);
    }

    private Object genValue(Element config, DirectiveMojo mojo, Object obj) throws LinkException {
        if (obj == null) {
            return null;
        }
        if (!Texts.hasText(Texts.toStr(obj))) {
            //空字符串不转换
            return obj;
        }
        String srcType = Texts.firstHasText(mojo.getParser().getParam(config, "srcType"), "long");
        String srcFmt = Texts.firstHasText(mojo.getParser().getParam(config, "srcFmt"), "yyyyMMddHHmmss");
        String destType = Texts.firstHasText(mojo.getParser().getParam(config, "destType"), "string");
        String destFmt = Texts.firstHasText(mojo.getParser().getParam(config, "destFmt"), "yyyyMMddHHmmss");
        if (srcType.equalsIgnoreCase("auto")) {
            if (obj instanceof Number) {
                srcType = "long";
            } else if (obj instanceof Date) {
                srcType = "date";
            }
        }
        DateTime middle = null;
        if (srcType.equalsIgnoreCase("string")) {
            String tmp = Texts.toStr(obj).trim();
            middle = DateTimeFormat.forPattern(srcFmt).parseDateTime(tmp);
        } else if (srcType.equalsIgnoreCase("long")) {
            Long tmp = Long.parseLong(Texts.toStr(obj).trim());
            middle = new DateTime(tmp);
        } else if (srcType.equalsIgnoreCase("date")) {
            Date tmp = (Date) obj;
            middle = new DateTime(tmp.getTime());
        } else if (srcType.equalsIgnoreCase("auto")) {
            Date tmp = DateTimeTool.tryParse(Texts.toStr(obj).trim());
            middle = new DateTime(tmp.getTime());
        } else {
            throw new LinkException("不支持的srcType: " + srcType);
        }

        if (destType.equalsIgnoreCase("string")) {
            return middle.toString(destFmt);
        } else if (destType.equalsIgnoreCase("long")) {
            return middle.getMillis();
        } else if (destType.equalsIgnoreCase("date")) {
            return middle.toDate();
        } else {
            throw new LinkException("不支持的destType: " + destType);
        }
    }
}
