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
        pot.put("type", "21");
        pot.put("no", "1000311");
        dm.callDirective("emp.dictCheck", pot);
    }
    @Test
    public void test2() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("type", "21");
        pot.put("no", "1000311");
        dm.callDirective("emp.dictCheck2", pot);

    }
    @Test
    public void test22() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("gender", "21");
        pot.put("no", "1000311");
        dm.callDirective("emp.dictCheck2", pot);
    }
}
