package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import com.github.landyking.link.converter.DatePack;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.DateTimeTool;
import com.github.landyking.link.util.Texts;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.ConversionService;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/13.
 */
public class DateTimeConverter extends OutputOneByOneProcessor {
    private ConversionService conversionService;

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

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
        String srcType = mojo.getParser().getParamText(config, "srcType");
        String srcFmt = mojo.getParser().getParamText(config, "srcFmt");
        String destType = mojo.getParser().getParamText(config, "destType");
        String destFmt = mojo.getParser().getParamText(config, "destFmt");
        return conversionService.convert(new DatePack(obj, srcType, srcFmt, destType, destFmt), Object.class);
    }
}
