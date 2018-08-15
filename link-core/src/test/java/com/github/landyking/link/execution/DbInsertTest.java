package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import com.github.landyking.link.util.Texts;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: landy
 * @date: 2018-08-08 21:28
 */
public class DbInsertTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "d555");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        pot.put("deptName", "helloworld");
        mojo = getDm().callDirective("test.execution.dbInsert", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals("helloworld", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
    }

    @Test
    public void testSuccess2() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "x987");
        pot.put("deptName", "helloworld");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbInsert", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
        mojo = getDm().callDirective("test.execution.deleteDepart", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }

    @Test
    public void testSuccessWithSubSql() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "x987");
        pot.put("deptName", "helloworld");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbInsertWithSubSql", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        String s = Texts.toStr(SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        Assert.assertTrue(s.startsWith("name"));
        mojo = getDm().callDirective("test.execution.deleteDepart", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }

    @Test
    public void testAddEmployee() throws LinkException {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("empNo", "99999");
        pot.put("firstName", "Landy");
        pot.put("lastName","King" );
        pot.put("gender","F");
        pot.put("birthDate","1978-01-22" );
        pot.put("hireDate", "1978-01-22");
        DirectiveMojo mojo = getDm().callDirective("test.execution.addEmployee", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst2].finalValue"));
    }
}