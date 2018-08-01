package com.github.landyking.link.util;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.landyking.link.ValueBag;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtils {
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    /**
     * Default content type for JSONP: "application/javascript".
     */
    public static final String DEFAULT_JSONP_CONTENT_TYPE = "application/javascript";
    private final static Logger LOG = LoggerFactory.getLogger(JSONUtils.class);
    public static final ObjectMapper JSON = initJson();

    private static ObjectMapper initJson() {
        ObjectMapper tmp = new ObjectMapper();
//        tmp.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        tmp.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(ValueBag.class, new ValueBagSerializer());
        tmp.registerModule(module);
        return tmp;
    }

    public static String toStr(Object o) throws JsonProcessingException {
        return JSON.writeValueAsString(o);
    }

    public static JsonNode toJsonNode(String extraConfig, boolean returnNull, boolean throwException) {
        if (Texts.hasText(extraConfig)) {
            try {
                return JSON.readTree(extraConfig);
            } catch (IOException e) {
                if (throwException) {
                    Throwables.propagate(e);
                } else {
                    LOG.warn("parse to JsonNode error: " + extraConfig);
                }
            }
        }
        if (returnNull) {
            return null;
        } else {
            return JSON.createObjectNode();
        }
    }
}