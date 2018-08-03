package com.github.landyking.link;

import com.github.landyking.link.pot.EmptyInputPot;
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
        dm.callDirective("emp.complexOutputList", pot);
    }

    @Test
    public void opMap() throws Exception {

        EmptyInputPot pot = new EmptyInputPot();
        dm.callDirective("emp.complexOutputMap", pot);
    }
}
