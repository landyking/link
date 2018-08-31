package com.github.landyking.link.spel;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: landy
 * @date: 2018-08-23 15:41
 */
public class SpelBeanResolverTest extends TestH2Database {
    @Test
    public void test() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        DirectiveMojo mojo = getDm().callDirective("test.execution.SpelBeanResolverTest", pot);
        Assert.assertEquals("hello", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[rst1]"));
    }
}
