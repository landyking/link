package com.github.landyking.link.beetl;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelTool;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.Texts;
import org.beetl.core.GeneralVarTagBinding;
import org.springframework.util.Assert;

/**
 * Created by landy on 2018/5/11.
 */
public class IfTag extends GeneralVarTagBinding {
    @Override
    public void render() {
        Object name = getAttributeValue("attr");
        Assert.notNull(name, "attr can't null");
        DirectiveMojo dc = (DirectiveMojo) ctx.getGlobal("ctx");
        Assert.notNull(dc, "DirectiveContext can't null");
        String attrName = Texts.toStr(name);
        try {
            SpelPair sp = SpelUtils.getSpelPair(dc);
            Boolean value = sp.getExp().parseExpression(attrName).getValue(sp.getCtx(), Boolean.class);
            if (value) {
                doBodyRender();
            }
        } catch (Exception e) {
            throw new RuntimeException("IfTag处理属性" + attrName + "异常", e);
        }
    }
}
