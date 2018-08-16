package com.github.landyking.link.spel;

import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;

/**
 * @author: landy
 * @date: 2018-08-16 23:14
 */
public class SpelUtilsTest {
    @Test
    public void test1() throws Exception {
        ExpressionParser exp = new SpelExpressionParser();
        HashMap<String, Object> expRoot = Maps.newHashMap();
        expRoot.put("hello", "world");
        StandardEvaluationContext ctx = new StandardEvaluationContext(expRoot);
        try {
            ctx.registerFunction("newList", SpelTool.class.getDeclaredMethod("newList", Integer.class));
            ctx.registerFunction("hasText", Texts.class.getDeclaredMethod("hasText", Object.class));
            ctx.registerFunction("anyNull", Texts.class.getDeclaredMethod("anyNull", Object[].class));
            ctx.registerFunction("anyHasText", Texts.class.getDeclaredMethod("anyHasText", Object[].class));
        } catch (Exception e) {
            throw new LinkException("注册spel函数异常", e);
        }

        Boolean value = exp.parseExpression("#hasText(#root[hello])").getValue(ctx, Boolean.class);
        Assert.assertTrue(value);
        value = exp.parseExpression("#anyNull(#root[world],#root[hello])").getValue(ctx, Boolean.class);
        Assert.assertTrue(value);
        value = exp.parseExpression("#anyNull(#root[hello])").getValue(ctx, Boolean.class);
        Assert.assertFalse(value);
        value = exp.parseExpression("#anyHasText(#root[world],[hello])").getValue(ctx, Boolean.class);
        Assert.assertTrue(value);
    }
}