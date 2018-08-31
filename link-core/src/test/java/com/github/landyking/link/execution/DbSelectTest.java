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
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1]"));
        pot.put("deptName", "DevelopmentMama");
        mojo = getDm().callDirective("test.execution.dbInsert", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success]"));
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("DevelopmentMama", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1]"));
        Assert.assertEquals("DevelopmentMama", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst2]"));
    }

    @Test
    public void testFailure() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d0051");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1]"));
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst2]"));
    }
    @Test
    public void testListEmployee2() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("firstName", "Guoxiang");
        DirectiveMojo mojo = getDm().callDirective("test.execution.listEmployee2", pot);
        Assert.assertEquals("Ramsay", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][lastName]"));
        Assert.assertEquals("Guoxiang", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][firstName]"));
        Assert.assertEquals("Development", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][deptName]"));
        Assert.assertEquals("d005", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][deptNo]"));
        Assert.assertEquals("1962年07月14日", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][birthDate]"));
    }
    @Test
    public void testSuccessSubSql() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d001");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelectSubSql", pot);
        Assert.assertEquals("d001", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dept_no]"));
        Assert.assertEquals("Marketing", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[dept_name]"));
        Assert.assertEquals(2l, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[employeeCount]"));
    }
}