package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import com.github.landyking.link.util.Texts;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by landy on 2018/8/8.
 */
public class DbExecuteTest extends TestH2Database {

    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("deptNo", "x99x");
        String uuidDeptName = Texts.uuid();
        pot.put("deptName", uuidDeptName);
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbExecute", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[insert]"));
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[delete]"));
    }

}