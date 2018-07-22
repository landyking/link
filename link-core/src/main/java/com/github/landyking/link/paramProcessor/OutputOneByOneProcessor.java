package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * @author: landy
 * @date: 2018-07-22 13:20
 */
public abstract class OutputOneByOneProcessor extends AbstractParamProcessor {
    @Override
    public void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, ValueBag>> outList) {
        for (Map<String, ValueBag> one : outList) {
            ValueBag item = one.get(name);
            Assert.notNull(item, "输出参数" + name + "没有对应的结果项");
            processOutputOne(mojo,  param, config,one,name, item);
        }
    }

    protected abstract void processOutputOne(DirectiveMojo mojo, Element param, Element config, Map<String, ValueBag> one, String name, ValueBag item);
}
