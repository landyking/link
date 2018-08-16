package com.github.landyking.link.execution;

import com.github.landyking.link.AbstractExecution;
import com.github.landyking.link.AbstractExecutionFactory;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.Texts;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/8/15.
 */
public class AssertTrue implements AbstractExecutionFactory {
    @Override
    public String tag() {
        return "assertTrue";
    }

    @Override
    public AbstractExecution generate(Element element) throws LinkException {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
                String id = element.getAttribute("id");
                String message = element.getAttribute("message");
                String expression = element.getAttribute("expression");
                SpelPair sp = SpelUtils.getSpelPair(mojo);
                Boolean rst = sp.getExp().parseExpression(expression).getValue(sp.getCtx(), Boolean.class);
                if (!rst) {
                    if (!Texts.hasText(message)) {
                        message = id + "断言" + expression + "失败";
                    }
                    throw new LinkException(message);
                }
            }
        };
    }
}
