package com.github.landyking.link;

import com.github.landyking.link.pot.EmptyInputPot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by landy on 2018/7/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/link/linkCore.xml")
public class DirectiveDbSelectTest {
    @Resource
    private DirectiveManager dm;

    @Test
    public void listEmployee() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("firstName", "Landy,Landy2,Landy3");
        pot.put("page", "3");
//        pot.put("pageStart", "13");
        pot.put("limit", "10");
        dm.callDirective("emp.listEmployee", pot);
    }
}