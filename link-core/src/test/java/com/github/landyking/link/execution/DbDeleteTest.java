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
 * @date: 2018-08-08 21:27
 */
public class DbDeleteTest extends TestH2Database {
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
        mojo = getDm().callDirective("test.execution.dbDelete", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
        mojo = getDm().callDirective("test.execution.dbSelect", pot);
        Assert.assertEquals(null, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
    }

    @Test
    public void testFailure() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "x999");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbDelete", pot);
        Assert.assertEquals(false, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }

    @Test
    public void testDeleteEmployeeByName() throws LinkException {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("empNo", "99999");
        String firstName = "LandyAsdf";
        pot.put("firstName", firstName);
        pot.put("lastName", "King");
        pot.put("gender", "F");
        pot.put("birthDate", "1978-01-22");
        pot.put("hireDate", "1978-01-22");
        DirectiveMojo mojo = getDm().callDirective("test.execution.addEmployee", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        pot = new EmptyInputPot();
        pot.put("firstName", firstName + ",unknown111");
        mojo = getDm().callDirective("test.execution.deleteEmployeeByName", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[success].finalValue"));
    }
}