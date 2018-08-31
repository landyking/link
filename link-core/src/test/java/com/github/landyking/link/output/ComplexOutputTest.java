package com.github.landyking.link.output;

import com.github.landyking.link.DirectiveManager;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by landy on 2018/8/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/link/linkCore.xml")
public class ComplexOutputTest {
    @Resource
    private DirectiveManager dm;

    @Test
    public void opList() throws Exception {

        EmptyInputPot pot = new EmptyInputPot();
        pot.put("name", "刘二");
        pot.put("age", "22");
        pot.put("address", "东大街18号");
        pot.put("hello", "world,big");
        DirectiveMojo mojo = dm.callDirective("test.output.complexOutputList", pot);
        Object afterOutput = mojo.getAfterOutput();
        Assert.assertEquals("刘二", SpelTool.getValueFromExpress(afterOutput, "#root[0][name]"));
        Assert.assertEquals("22", SpelTool.getValueFromExpress(afterOutput, "#root[0][age]"));
        Assert.assertEquals("东大街18号", SpelTool.getValueFromExpress(afterOutput, "#root[0][address]"));
        Assert.assertEquals("world", SpelTool.getValueFromExpress(afterOutput, "#root[0][hello][0]"));
        Assert.assertEquals("big", SpelTool.getValueFromExpress(afterOutput, "#root[0][hello2][1]"));
    }

    @Test
    public void opMap() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        dm.callDirective("test.output.complexOutputMap", pot);
    }
}
