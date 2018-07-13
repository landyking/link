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

    @Test
    public void addEmployee() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("empNo", "990003");
        pot.put("birthDate", "19530421");
        pot.put("firstName", "Landy");
        pot.put("lastName", "King");
        pot.put("gender", "F");
        pot.put("hireDate", "1987-09-21");
        dm.callDirective("emp.addEmployee", pot);
        pot.put("empNo", "990004");
        pot.put("firstName", "Landy2");
        dm.callDirective("emp.addEmployee", pot);
        pot.put("empNo", "990002");
        pot.put("firstName", "Landy3");
        dm.callDirective("emp.addEmployee", pot);
    }

    @Test
    public void deleteEmployee() throws Exception {
        EmptyInputPot pot = new EmptyInputPot();
        pot.put("firstName", "Landy,Landy2,Landy3");
        dm.callDirective("emp.deleteEmployee", pot);
    }
}