package com.github.landyking.link.beetl;

import org.beetl.core.Template;
import org.junit.Test;


/**
 * @author: landy
 * @date: 2018-07-25 20:42
 */
public class BeetlToolTest {
    @Test
    public void test1() throws Exception {
        Template tmp = BeetlTool.getTemplate()
                .getTemplate("调用${指令编码}");
        tmp.binding("指令编码", "test");
        System.out.println(tmp.render());

    }
}