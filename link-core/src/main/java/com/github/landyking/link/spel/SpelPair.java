package com.github.landyking.link.spel;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Created by landy on 2018/8/8.
 */
public class SpelPair {
    ExpressionParser exp;
    StandardEvaluationContext ctx;

    public ExpressionParser getExp() {
        return exp;
    }

    public void setExp(ExpressionParser exp) {
        this.exp = exp;
    }

    public StandardEvaluationContext getCtx() {
        return ctx;
    }

    public void setCtx(StandardEvaluationContext ctx) {
        this.ctx = ctx;
    }

    public SpelPair(ExpressionParser exp, StandardEvaluationContext ctx) {
        this.exp = exp;
        this.ctx = ctx;
    }
}
