package com.github.landyking.link.beetl;

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
        if ((Boolean) name) {
            doBodyRender();
        } else {

        }
    }
}
