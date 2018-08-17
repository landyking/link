package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import com.github.landyking.link.util.DateTimeTool;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Created by landy on 2018/8/6.
 */
public class LikeParamGeneratorTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("like", "world");
        pot.put("like2", "world");
        pot.put("likeLeft", "world");
        pot.put("likeRight", "world");
        pot.put("likeBoth", "world");
        DirectiveMojo mojo = getDm().callDirective("test.param.LikeParamGenerator", pot);
        Assert.assertEquals("%world%", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[like].finalValue"));
        Assert.assertEquals("%world%", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[likeBoth].finalValue"));
        Assert.assertEquals("%world%", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[likeBoth2].finalValue"));
        Assert.assertEquals("%world", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[likeLeft].finalValue"));
        Assert.assertEquals("%world", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[likeLeft2].finalValue"));
        Assert.assertEquals("world%", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[likeRight].finalValue"));
        Assert.assertEquals("world%", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[likeRight2].finalValue"));
        Assert.assertNull(SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[likeEmpty].finalValue"));
    }
}