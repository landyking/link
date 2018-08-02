package com.github.landyking.link.converter;


import com.github.landyking.link.util.DateTimeTool;
import com.github.landyking.link.util.Texts;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Created by landy on 2018/8/2.
 */
public class DatePackConverter implements Converter<DatePack, Object> {
    @Override
    public Object convert(DatePack source) {
        Object obj = source.getValue();
        if (obj == null) {
            return null;
        }
        String srcType = Texts.firstHasText(source.getSrcType(), "long");
        String srcFmt = Texts.firstHasText(source.getSrcFormat(), "yyyyMMddHHmmss");
        String destType = Texts.firstHasText(source.getDestType(), "string");
        String destFmt = Texts.firstHasText(source.getDestFormat(), "yyyyMMddHHmmss");
        if ("auto".equalsIgnoreCase(srcType)) {
            if (obj instanceof Number) {
                srcType = "long";
            } else if (obj instanceof Date) {
                srcType = "date";
            } else if (obj instanceof DateTime) {
                srcType = "datetime";
            }
        }
        DateTime middle = null;
        if ("string".equalsIgnoreCase(srcType)) {
            String tmp = Texts.toStr(obj).trim();
            middle = DateTimeFormat.forPattern(srcFmt).parseDateTime(tmp);
        } else if (srcType.equalsIgnoreCase("long")) {
            Long tmp = Long.parseLong(Texts.toStr(obj).trim());
            middle = new DateTime(tmp);
        } else if (srcType.equalsIgnoreCase("date")) {
            Date tmp = (Date) obj;
            middle = new DateTime(tmp.getTime());
        } else if ("datetime".equalsIgnoreCase(srcType)) {
            middle = (DateTime) obj;
        } else if (srcType.equalsIgnoreCase("auto")) {
            Date tmp = DateTimeTool.tryParse(Texts.toStr(obj).trim());
            Assert.notNull(tmp, "无法自动识别源值类型");
            middle = new DateTime(tmp.getTime());
        } else {
            throw new IllegalArgumentException("不支持的srcType: " + srcType);
        }

        if (destType.equalsIgnoreCase("string")) {
            return middle.toString(destFmt);
        } else if (destType.equalsIgnoreCase("long")) {
            return middle.getMillis();
        } else if (destType.equalsIgnoreCase("date")) {
            return middle.toDate();
        } else {
            throw new IllegalArgumentException("不支持的destType: " + destType);
        }
    }
}
