package com.github.landyking.link.beetl;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.Texts;
import com.google.common.base.Throwables;
import org.beetl.core.GeneralVarTagBinding;
import org.springframework.util.Assert;

/**
 * Created by landy on 2018/5/11.
 */
public class ParamTag extends GeneralVarTagBinding {
    @Override
    public void render() {
        Object name = getAttributeValue("attr");
        Assert.notNull(name, "attr can't null");
        DirectiveMojo dc = (DirectiveMojo) ctx.getGlobal("ctx");
        Assert.notNull(dc, "DirectiveContext can't null");
        String attrName = Texts.toStr(name);
        boolean doRender = false;
        if (attrName.startsWith("#root")) {
            try {
                SpelPair sp = SpelUtils.getSpelPair(dc);
                Object value = sp.getExp().parseExpression(attrName).getValue(sp.getCtx());
                doRender = value != null;
                if (doRender) {
                    if (value instanceof CharSequence) {
                        doRender = Texts.hasText(value);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("ParamTag处理属性" + attrName + "异常", e);
            }
        } else {
            doRender = Texts.hasText(dc.getProcessedInputParam(attrName));
        }
        if (doRender) {
            doBodyRender();
        } else {
        }
    }
}
