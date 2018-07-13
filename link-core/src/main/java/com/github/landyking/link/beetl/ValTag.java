package com.github.landyking.link.beetl;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.Texts;
import org.beetl.core.GeneralVarTagBinding;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Created by landy on 2018/5/11.
 */
public class ValTag extends GeneralVarTagBinding {
    @Override
    public void render() {
        Object name = getAttributeValue("attr");
        Assert.notNull(name, "attr can't null");
        DirectiveMojo dc = (DirectiveMojo) ctx.getGlobal("ctx");
        Assert.notNull(dc, "DirectiveContext can't null");
        Object value = dc.getProcessedInputParam(name.toString());
        try {
            ctx.byteWriter.writeString(Texts.toStr(value, ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
