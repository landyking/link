package com.github.landyking.link;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by landy on 2018/7/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/link/linkCore.xml")
public class DirectiveManagerTest {
    @Resource
    private DirectiveManager dm;
    @Test
    public void callDirective() throws Exception {
        dm.callDirective("hello.world", new InputPot());
    }

}