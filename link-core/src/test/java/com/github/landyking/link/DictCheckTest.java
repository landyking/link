package com.github.landyking.link;

import com.github.landyking.link.pot.EmptyInputPot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author: landy
 * @date: 2018-07-30 23:09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/link/linkCore.xml")
public class DictCheckTest {
    @Resource
    private DirectiveManager dm;

    @Test
    public void test1() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("type", "212");
        pot.put("no", "10003");
        dm.callDirective("emp.dictCheck", pot);

    }
}
