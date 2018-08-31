package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: landy
 * @date: 2018-08-16 22:54
 */
public class AssertTrueTest extends TestH2Database {
    @Test
    public void testFailure() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("first", "hello");
        DirectiveMojo mojo = getDm().callDirective("test.execution.assert1", pot);
        Assert.assertNotNull(mojo.getException());
        Assert.assertEquals("入参second为空", mojo.getException().getMessage());
    }
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("first", "hello");
        pot.put("second", "hello");
        DirectiveMojo mojo = getDm().callDirective("test.execution.assert1", pot);
        Assert.assertEquals(true, SpelTool.getValueFromExpress(mojo.getAfterOutput(),"[success]"));
    }
}
