package com.github.landyking.link.beetl;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.Texts;
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
        if (Texts.hasText(dc.getProcessedInputParam(name.toString()))) {
            doBodyRender();
        } else {
        }
    }
}
