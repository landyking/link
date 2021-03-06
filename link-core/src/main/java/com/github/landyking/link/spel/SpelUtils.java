package com.github.landyking.link.spel;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
import com.google.common.collect.Maps;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by landy on 2018/8/8.
 */
public class SpelUtils {
    public static SpelPair getSpelPair(DirectiveMojo mojo) throws LinkException {
        ExpressionParser exp = new SpelExpressionParser();
        HashMap<String, Object> expRoot = Maps.newHashMap();
        expRoot.put("mojo", mojo);
        expRoot.put("input", mojo.getProcessedInputParamMap());
        expRoot.put("exec", mojo.getExecuteResultMap());
        expRoot.put("end", mojo.getEndingData());
        StandardEvaluationContext ctx = new StandardEvaluationContext(expRoot);
        ctx.setBeanResolver(new BeanFactoryResolver(mojo.getApplicationContext()));
        try {
            ctx.registerFunction("newList", SpelTool.class.getDeclaredMethod("newList", Integer.class));
            ctx.registerFunction("toInt", SpelTool.class.getDeclaredMethod("toInt", String.class));
            ctx.registerFunction("now", SpelTool.class.getDeclaredMethod("now"));
            ctx.registerFunction("plusDays", SpelTool.class.getDeclaredMethod("plusDays", Date.class, int.class));
            ctx.registerFunction("hasText", Texts.class.getDeclaredMethod("hasText", Object.class));
            ctx.registerFunction("notNull", Texts.class.getDeclaredMethod("notNull", Object.class));
            ctx.registerFunction("uuid", Texts.class.getDeclaredMethod("uuid"));
            ctx.registerFunction("isNull", Texts.class.getDeclaredMethod("isNull", Object.class));
            ctx.registerFunction("anyNull", Texts.class.getDeclaredMethod("anyNull", Object[].class));
            ctx.registerFunction("allHasText", Texts.class.getDeclaredMethod("allHasText", Object[].class));
            ctx.registerFunction("anyHasText", Texts.class.getDeclaredMethod("anyHasText", Object[].class));
            ctx.registerFunction("contact", Texts.class.getDeclaredMethod("contact", Object[].class));
        } catch (Exception e) {
            throw new LinkException("注册spel函数异常", e);
        }
        return new SpelPair(exp, ctx);
    }
}
