package com.github.landyking.link.spel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by landy on 2018/8/6.
 */
public class SpelTool {
    public static List<Object> newList(Integer size) {
        if (size == null) {
            size = 0;
        }
        ArrayList<Object> objects = Lists.newArrayListWithCapacity(size);
        for (int i = 0; i < size; i++) {
            objects.add(i);
        }
        return objects;
    }

    public static Object getValueFromExpress(Object root, String express) {
        ExpressionParser exp = new SpelExpressionParser();
        return exp.parseExpression(express).getValue(root);
    }
}
