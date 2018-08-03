package com.github.landyking.link.springel;

import com.github.landyking.link.ExecuteResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;

/**
 * Created by landy on 2018/8/3.
 */
public class SpringelTest {
    @Test
    public void test1() throws Exception {
        ExpressionParser exp = new SpelExpressionParser();
        HashMap<String, Object> expRoot = Maps.newHashMap();
        HashMap<String, Object> value = Maps.newHashMap();
        ExecuteResult er = new ExecuteResult();
        er.setTotalCount(100);
        er.setPrimaryKeyValue("222");
        List<Map<String, Object>> maps = Lists.newArrayList();
        maps.add(Collections.singletonMap("hello", (Object) "你好"));
        maps.add(Collections.singletonMap("hello", (Object) "世界"));
        er.setData(maps);
        value.put("default", er);
        expRoot.put("exec", value);
        StandardEvaluationContext ctx = new StandardEvaluationContext(expRoot);
        Object rst = exp.parseExpression("[exec][default].data.![#this[hello]]").getValue(ctx);
        System.out.println(rst);
    }

    @Test
    public void test2() throws Exception {
        ExpressionParser exp = new SpelExpressionParser();
        HashMap<String, Object> expRoot = Maps.newHashMap();
        expRoot.put("data", Arrays.asList(Collections.singletonMap("age", Arrays.asList(11, 12, 13))));
        StandardEvaluationContext ctx = new StandardEvaluationContext(expRoot);
        Object rst = exp.parseExpression("[data].![#this[age]][0]").getValue(ctx);
        System.out.println(rst);
    }
    @Test
    public void test3() throws Exception {
        ExpressionParser exp = new SpelExpressionParser();
        HashMap<String, Object> expRoot = Maps.newHashMap();
        expRoot.put("data", Arrays.asList(11, 12, 13));
        StandardEvaluationContext ctx = new StandardEvaluationContext(expRoot);
        Object rst = exp.parseExpression("new java.util.ArrayList([data].size())").getValue(ctx);
        System.out.println(rst);
    }
}
