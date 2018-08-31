package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.converter.DatePack;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
import org.springframework.core.convert.ConversionService;
import org.w3c.dom.Element;

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
    protected Object processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, Object> one, String name, Object item) throws LinkException {
        Object val = genValue(config, mojo, item);
        return val;
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
