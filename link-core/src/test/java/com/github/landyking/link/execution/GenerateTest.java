package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by landy on 2018/8/15.
 */
public class GenerateTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("name", "d005,d1112");
        pot.put("now", "2018-01-02");
        DirectiveMojo mojo = getDm().callDirective("test.execution.generate", pot);
        Assert.assertEquals("2018-01-02 00:00:00",SpelTool.getValueFromExpress(mojo.getAfterOutput(),"#root[now].finalValue"));
        Assert.assertEquals("d1112",SpelTool.getValueFromExpress(mojo.getAfterOutput(),"#root[name][1].finalValue"));
    }
}