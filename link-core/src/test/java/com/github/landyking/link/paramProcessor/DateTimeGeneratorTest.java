package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import com.github.landyking.link.util.DateTimeTool;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by landy on 2018/8/6.
 */
public class DateTimeGeneratorTest extends TestH2Database {
    @Test
    public void testSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        Date before = new Date();
        String value = DateTimeTool.fullText(before);
        pot.put("time1", value);
        pot.put("time2", "empty");
        DirectiveMojo mojo = getDm().callDirective("test.input.DateTimeGenerator", pot);
        Date after = new Date();
        Assert.assertEquals(value, SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[time1].finalValue"));
        Date gen = (Date) SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[time2].finalValue.toDate()");
        Assert.assertTrue(gen.getTime() >= before.getTime() && gen.getTime() <= after.getTime());
    }
}