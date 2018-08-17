package com.github.landyking.link.spel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Map;

/**
 * Created by landy on 2018/8/8.
 */
public class SpelMapSqlParameterSource extends MapSqlParameterSource {
    private static final Logger logger = LoggerFactory.getLogger(SpelMapSqlParameterSource.class);
    private final SpelPair spelPair;

    public SpelMapSqlParameterSource(Map<String, ?> values, SpelPair spelPair) {
        super(values);
        this.spelPair = spelPair;
    }

    @Override
    public boolean hasValue(String paramName) {
        boolean b = super.hasValue(paramName);
        if (!b) {
            if (paramName.startsWith("#")) {
                try {
                    Object value = spelPair.getExp().parseExpression(paramName).getValue(spelPair.getCtx());
                    addValue(paramName, value);
                    return true;
                } catch (Exception e) {
                    logger.warn("尝试解析SQL命名参数{}的值失败", paramName, e);
                }
            }
        }
        return b;
    }

    @Override
    public Object getValue(String paramName) {
        return super.getValue(paramName);
    }
}
