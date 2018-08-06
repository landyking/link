package com.github.landyking.link;

import com.github.landyking.link.pot.EmptyInputPot;
import com.github.landyking.link.spel.SpelTool;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: landy
 * @date: 2018-07-30 23:09
 */
public class DictCheckTest extends TestH2Database {


    @Test
    public void testDuplicateCheckSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("no", "1000311");
        DirectiveMojo mojo = getDm().callDirective("test.input.DuplicateCheck", pot);
        Assert.assertEquals("1000311", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][no].finalValue"));
    }

    @Test
    public void testDuplicateCheckError() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("no", "10122");
        DirectiveMojo mojo = getDm().callDirective("test.input.DuplicateCheck", pot);
        Assert.assertNotNull(mojo.getException());
        Assert.assertEquals("处理入参[no:员工编号]异常，指定信息已存在1条", mojo.getException().getMessage());
    }
    @Test
    public void testExistCheckSuccess() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("type", "21");
        DirectiveMojo mojo = getDm().callDirective("test.input.ExistCheck", pot);
        Assert.assertEquals("21", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][type]?.finalValue"));
        pot.put("type", "22");
        mojo = getDm().callDirective("test.input.ExistCheck", pot);
        Assert.assertEquals("22", SpelTool.getValueFromExpress(mojo.getAfterOutput(), "#root[0][type]?.finalValue"));
    }
    @Test
    public void testExistCheckError() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("type", "44");
        DirectiveMojo mojo = getDm().callDirective("test.input.ExistCheck", pot);
        Assert.assertNotNull(mojo.getException());
        Assert.assertEquals("处理入参[type:类型]异常，字典hello中不存在项44", mojo.getException().getMessage());
        pot.put("type", "45");
        mojo = getDm().callDirective("test.input.ExistCheck", pot);
        Assert.assertNotNull(mojo.getException());
        Assert.assertEquals("处理入参[type:类型]异常，字典hello中不存在项45", mojo.getException().getMessage());
    }

    @Test
    public void test22() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("gender", "21");
        pot.put("no", "1000311");
        getDm().callDirective("emp.dictCheck2", pot);
    }
}
