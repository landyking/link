package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by landy on 2018/8/8.
 */
public class DbUpdateTest extends TestH2Database {

    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d005");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("Development", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));
        pot.put("deptName", "MamaMiya");
        mojo = getDm().callDirective("test.execution.dbUpdate", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("MamaMiya", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));

        pot.put("deptNo", "d005");
        pot.put("deptName", "Development");
        mojo = getDm().callDirective("test.execution.dbUpdate2", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }

    @Test
    public void testFailure() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d0051");
        pot.put("deptName", "MamaMiya");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbUpdate", pot);
        Assert.assertEquals(false, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }

    @Test
    public void testSuccess2() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d005");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("Development", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));
        pot.put("deptNo", "d005");
        pot.put("deptName", "MamaMiya");
        mojo = getDm().callDirective("test.execution.dbUpdate2", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));

        pot.put("deptNo", "d005");
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("MamaMiya", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));


        pot.put("deptNo", "d005");
        pot.put("deptName", "Development");
        mojo = getDm().callDirective("test.execution.dbUpdate2", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }

    @Test
    public void testSuccess22() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d005");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("Development", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));
        pot.put("deptNo", "d004");
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("Production", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));
        pot.put("deptNo", "d005#d004");
        pot.put("deptName", "MamaMiya");
        pot.put("notIn", "d005");
        mojo = getDm().callDirective("test.execution.dbUpdate2", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));

        pot.put("deptNo", "d005");
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("Development", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));
        pot.put("deptNo", "d004");
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("MamaMiya", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));


        pot.put("deptNo", "d004");
        pot.put("deptName", "Production");
        mojo = getDm().callDirective("test.execution.dbUpdate2", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }

    @Test
    public void testSuccess21() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d005");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("Development", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));
        pot.put("deptNo", "d005");
        pot.put("deptName", "MamaMiya");
        pot.put("notIn", "d005");
        mojo = getDm().callDirective("test.execution.dbUpdate2", pot);
        Assert.assertEquals(false, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));

        pot.put("deptNo", "d005");
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("Development", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0]"));

    }
}