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
    public void addDepartment() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("departmentNo", "d013");
        pot.put("departmentName", "UI");
        dm.callDirective("emp.addDepartment", pot);
    }

    @Test
    public void updateDepartment() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("departmentNo", "d013");
        pot.put("departmentName", "UI");
        pot.put("employeeGender", "F");
        dm.callDirective("emp.updateDepartment", pot);
    }

}