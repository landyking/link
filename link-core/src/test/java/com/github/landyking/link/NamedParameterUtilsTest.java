package com.github.landyking.link;

import com.github.landyking.link.spel.SpelTool;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

/**
 * Created by landy on 2018/8/8.
 */
public class NamedParameterUtilsTest {
    @Test
    public void test1() throws Exception {
        String sql = "select * from a where a.b=:#root?[exec]?[default]?.data.![#this[a]] and ";
        ParsedSql psql = NamedParameterUtils.parseSqlStatement(sql);
        System.out.println(psql);
    }
    @Test
    public void test2() throws Exception {
        String sql = "dept_no = :{#root[exec][first].data[0]?.get('dept_no')}";
        ParsedSql psql = NamedParameterUtils.parseSqlStatement(sql);
        System.out.println(psql);
    }
}
