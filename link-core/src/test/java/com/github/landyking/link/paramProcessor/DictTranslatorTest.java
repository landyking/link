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
public class DictTranslatorTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("dict1", "21");
        pot.put("dict2", "a");
        DirectiveMojo mojo = getDm().callDirective("test.param.DictTranslator", pot);
        Assert.assertEquals("我也好", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dict1]"));
        Assert.assertEquals("大写A", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dict2]"));
        pot.put("dict1", "22");
        pot.put("dict2", "b");
        mojo = getDm().callDirective("test.param.DictTranslator", pot);
        Assert.assertEquals("我也好2", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dict1]"));
        Assert.assertEquals("大写B", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dict2]"));
    }

    @Test
    public void testFailure() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("dict1", "212");
        pot.put("dict2", "a");
        DirectiveMojo mojo = getDm().callDirective("test.param.DictTranslator", pot);
        Assert.assertEquals("212", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dict1]"));
        Assert.assertEquals("大写A", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dict2]"));
    }

}