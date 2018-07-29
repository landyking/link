package com.github.landyking.link;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * @author: landy
 * @date: 2018-07-29 22:53
 */
public class LocalDictManagerTest {
    @Test
    public void test1() throws Exception {
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext();
        Resource[] res = app.getResources("classpath*:/link/dict/**/*.xml");
        for (Resource one : res) {
            System.out.println(one.getFile().getAbsolutePath());
        }

    }
}