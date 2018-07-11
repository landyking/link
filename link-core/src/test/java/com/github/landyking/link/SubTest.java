package com.github.landyking.link;

import org.junit.Assert;
import org.junit.Test;

import java.net.URLDecoder;


/**
 * Created by landy on 2018/6/27.
 */
public class SubTest {

    @Test
    public void test1() throws Exception {
        String a = "name=22%2B2&age=%E7%A9%BA+%E6%A0%BC&%E4%B8%AD%E6%96%87=%E5%A7%93%E5%90%8D";
        String decode = URLDecoder.decode(a, "utf8");
        Assert.assertEquals("name=22+2&age=空 格&中文=姓名", decode);
    }
}