package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by landy on 2018/8/15.
 */
public class CustomExecutionTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("time", "2018-02-11");
        DirectiveMojo mojo = getDm().callDirective("test.execution.custom", pot);
        Assert.assertEquals("2018年02月11日", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[result]"));
    }
}