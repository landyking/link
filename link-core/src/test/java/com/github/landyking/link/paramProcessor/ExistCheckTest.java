package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by landy on 2018/8/6.
 */
public class ExistCheckTest extends TestH2Database{
    @Test
    public void testExistCheckSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("type", "21");
        pot.put("no", "10121");
        pot.put("firstName", "10121");
        DirectiveMojo mojo = getDm().callDirective("test.param.ExistCheck", pot);
        Assert.assertEquals("21", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][type]"));
        pot.put("type", "22");
        mojo = getDm().callDirective("test.param.ExistCheck", pot);
        Assert.assertEquals("22", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][type]"));
    }
    @Test
    public void testExistCheckError() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("type", "44");
        pot.put("no", "10121");
        pot.put("firstName", "10121");
        DirectiveMojo mojo = getDm().callDirective("test.param.ExistCheck", pot);
        Assert.assertNotNull(mojo.getException());
        Assert.assertEquals("处理入参[type:类型]异常，字典hello中不存在项44", mojo.getException().getMessage());
        pot.put("type", "45");
        mojo = getDm().callDirective("test.param.ExistCheck", pot);
        Assert.assertNotNull(mojo.getException());
        Assert.assertEquals("处理入参[type:类型]异常，字典hello中不存在项45", mojo.getException().getMessage());
    }

}