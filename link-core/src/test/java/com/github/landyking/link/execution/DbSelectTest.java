package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import com.github.landyking.link.util.DateTimeTool;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by landy on 2018/8/8.
 */
public class DbSelectTest extends TestH2Database {

    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "x999");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        pot.put("deptName", "DevelopmentMama");
        mojo = getDm().callDirective("test.execution.dbInsert", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("DevelopmentMama", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        Assert.assertEquals("DevelopmentMama", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst2].finalValue"));
    }

    @Test
    public void testFailure() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d0051");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst2].finalValue"));
    }
}