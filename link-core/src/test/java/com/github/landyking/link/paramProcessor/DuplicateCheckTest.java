package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.TestH2Database;
import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by landy on 2018/8/6.
 */
public class DuplicateCheckTest extends TestH2Database{

    @Test
    public void testDuplicateCheckSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("no", "1000311");
        pot.put("firstName", "10122");
        DirectiveMojo mojo = getDm().callDirective("test.param.DuplicateCheck", pot);
        Assert.assertEquals("1000311", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][no]"));
    }

    @Test
    public void testDuplicateCheckError() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("no", "10122");
        pot.put("firstName", "10122");
        DirectiveMojo mojo = getDm().callDirective("test.param.DuplicateCheck", pot);
        Assert.assertNotNull(mojo.getException());
        Assert.assertEquals("处理入参[no:员工编号]异常，指定信息已存在1条", mojo.getException().getMessage());
    }
}