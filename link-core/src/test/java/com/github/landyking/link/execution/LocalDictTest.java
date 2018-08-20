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
public class LocalDictTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        DirectiveMojo mojo = getDm().callDirective("test.execution.localDictTest", pot);
        Assert.assertEquals(1, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1].finalValue"));
        Assert.assertEquals("1", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst11].finalValue"));
        Assert.assertEquals(2, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst2].finalValue"));
        Assert.assertEquals("2", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst21].finalValue"));
    }

}
