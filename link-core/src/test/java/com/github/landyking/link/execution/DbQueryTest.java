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
public class DbQueryTest extends TestH2Database {

    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("queryNo", "d001");
        DirectiveMojo mojo = getDm().callDirective("test.execution.dbQuery", pot);
        Assert.assertEquals(11, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[count].finalValue"));
        Assert.assertEquals("Marketing", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[name].finalValue"));
        Assert.assertEquals(3, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[size].finalValue"));
    }

}