package com.github.landyking.link;

import com.github.landyking.link.pot.EmptyInputPot;
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
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("name", "student");
        pot.put("description", "学生信息");
        pot.put("driverClass", "com.mysql.jdbc.Driver");
        pot.put("jdbcUrl", "jdbc:mysql://192.168.50.133:3306/test?useUnicode=true&amp;characterEncoding=utf-8&amp;autoReconnect=true&amp;failOverReadOnly=false");
        pot.put("username", "root");
        pot.put("password", "manager");
        dm.callDirective("dbm.addDs", pot);
    }

}