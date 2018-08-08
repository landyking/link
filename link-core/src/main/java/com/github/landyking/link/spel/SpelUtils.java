package com.github.landyking.link.spel;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.exception.LinkException;
import com.google.common.collect.Maps;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
        try {
            ctx.registerFunction("newList", SpelTool.class.getDeclaredMethod("newList", Integer.class));
        } catch (Exception e) {
            throw new LinkException("注册spel函数异常", e);
        }
        return new SpelPair(exp, ctx);
    }
}
