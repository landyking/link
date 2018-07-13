package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.util.Texts;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by landy on 2018/7/13.
 */
public class TextToList extends AbstractParamProcessor {
    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        if (in == null || !Texts.hasText(in)) {
            return in;
        }
        String delimiter = mojo.getParser().getParam(config, "delimiter");
        String destType = mojo.getParser().getParam(config, "destType");
        Iterable<String> arr = Splitter.on(delimiter).omitEmptyStrings().split(Texts.toStr(in, ""));
        List<Object> rst = Lists.newLinkedList();
        for (String a : arr) {
            rst.add(convert(a, destType));
        }
        return rst;
    }

    private Object convert(String a, String destType) {
        if (destType.equalsIgnoreCase("string")) {
            return a;
        } else if (destType.equalsIgnoreCase("long")) {
            return Longs.tryParse(a);
        } else if (destType.equalsIgnoreCase("int") || destType.equalsIgnoreCase("integer")) {
            return Ints.tryParse(a);
        } else if (destType.equalsIgnoreCase("float") || destType.equalsIgnoreCase("double")) {
            return Doubles.tryParse(a);
        }
        return a;
    }
}
