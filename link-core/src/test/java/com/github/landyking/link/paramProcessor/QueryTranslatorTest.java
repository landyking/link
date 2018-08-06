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
public class QueryTranslatorTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d002");
        DirectiveMojo mojo = getDm().callDirective("test.param.QueryTranslator", pot);
        Assert.assertEquals("Finance", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[deptName]?.finalValue"));
        pot.put("deptNo", "d003");
        mojo = getDm().callDirective("test.param.QueryTranslator", pot);
        Assert.assertEquals("Human Resources", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[deptName]?.finalValue"));
    }
    @Test
    public void testFailure() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d0021");
        DirectiveMojo mojo = getDm().callDirective("test.param.QueryTranslator", pot);
        Assert.assertEquals("d0021", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[deptName]?.finalValue"));
        pot.put("deptNo", "d0031");
        mojo = getDm().callDirective("test.param.QueryTranslator", pot);
        Assert.assertEquals("d0031", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[deptName]?.finalValue"));
    }
    @Test
    public void testSuccessWithCondition() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d002");
        DirectiveMojo mojo = getDm().callDirective("test.param.QueryTranslator", pot);
        Assert.assertEquals("Finance", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[deptName]?.finalValue"));
        pot.put("notName", "Finance");
        pot.put("deptNo", "d002");
        mojo = getDm().callDirective("test.param.QueryTranslator", pot);
        Assert.assertEquals("d002", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[deptName]?.finalValue"));
    }
}