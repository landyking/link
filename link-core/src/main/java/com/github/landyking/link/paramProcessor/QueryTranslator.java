package com.github.landyking.link.paramProcessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import com.github.landyking.link.util.LkTools;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: landy
 * @date: 2018-07-22 20:02
 */
public class QueryTranslator extends AbstractParamProcessor {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    public static final String SPLIT = "#";
    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, ValueBag>> outList) throws Exception {
        String businessModelCode = getBusinessModelCode(mojo, config);
        String srcFieldName = getSrcFieldName(mojo, config);
        String destFieldName = getDestFieldName(mojo, config);
        String whereCondition = getWhereCondition(mojo, config);
        String displayFieldName = getDisplayFieldName(mojo, config);
        Boolean failUseOriginal = getFailUseOriginal(mojo, config);
        String cacheName = getCacheName(businessModelCode, destFieldName, displayFieldName, whereCondition, config);
        boolean containsCache = cacheManager.getCacheNames().contains(cacheName);
        Map<String, Object> transMap = null;
        boolean allMatch = true;
        if (containsCache) {
            //缓存存在
            Cache cache = cacheManager.getCache(cacheName);
            transMap = Maps.newHashMap();
            //检查要翻译的项是否都能匹配
            for (Map<String, ResultItem> one : rstList) {
                ResultItem srcItem = one.get(srcFieldName);
                Object ov = srcItem.getResultValue();
                if (ov != null) {
                    String key = ov.toString();
                    Cache.ValueWrapper wrapper = cache.get(key);
                    if (wrapper == null) {
                        allMatch = false;
                        break;//一旦出现不匹配，直接中止。
                    } else {
                        transMap.put(key, wrapper.get());
                    }
                }
            }
            if (!allMatch) {
                //不能匹配，清理已填充的内容，从数据库加载
                transMap.clear();
                transMap = makeTransMap(ctx, config, rstList, businessModelCode, srcFieldName, destFieldName, whereCondition, displayFieldName);
            } else {
                LOG.debug("使用缓存{}进行翻译", cacheName);
            }
        } else {
            //缓存不存在，从数据库加载
            transMap = makeTransMap(ctx, config, rstList, businessModelCode, srcFieldName, destFieldName, whereCondition, displayFieldName);
        }
        if (transMap == null) return;
        if (!containsCache || !allMatch) {
            //缓存不存在 或者 不能完全匹配，需要更新缓存。
            Cache cache = cacheManager.getCache(cacheName);
            for (Map.Entry<String, Object> one : transMap.entrySet()) {
                cache.put(one.getKey(), one.getValue());
            }
        }
        //使用最终的map进行翻译
        for (Map<String, ResultItem> one : rstList) {
            ResultItem srcItem = one.get(srcFieldName);
            ResultItem outItem = one.get(param.getName());
            Object ov = srcItem.getResultValue();
            if (ov != null) {
                Object out = transMap.get(ov.toString());
                if (out != null) {
                    outItem.setFinalValue(out);
                } else {
                    if (failUseOriginal) {
                        outItem.setFinalValue(ov);
                    } else {
                        outItem.setFinalValue(null);
                    }
                }
                outItem.setValueProcessed(true);
            }
        }
    }

    private Boolean getFailUseOriginal(DirectiveMojo mojo, Element config) {
        return LkTools.isTrue(mojo.getParser().getParam(config, "failUseOriginal"));
    }

    protected String getCacheName(String businessModelCode, String destFieldName, String displayFieldName, String whereCondition, JsonNode config) {
        String name = businessModelCode + SPLIT + destFieldName + SPLIT + displayFieldName;
        if (Texts.hasText(whereCondition)) {
            name += SPLIT + Hashing.md5().hashString(whereCondition, Charsets.UTF_8).toString();
        }
        return name;
    }

    protected Map<String, Object> makeTransMap(DirectiveContext ctx, JsonNode config, List<Map<String, ResultItem>> rstList, String businessModelCode, String srcFieldName, String destFieldName, String whereCondition, String displayFieldName) throws DirectiveExecutionException {
        Set<Object> inSet = Sets.newHashSet();
        Set<String> inSetString = Sets.newHashSet();
        for (Map<String, ResultItem> one : rstList) {
            ResultItem item = one.get(srcFieldName);
            Assert.notNull(item, "参数" + srcFieldName + "对应结果项为空");
            if (item.getResultValue() != null) {
                inSet.add(item.getResultValue());
                inSetString.add(item.getResultValueString());
            }
        }
        if (inSet.isEmpty()) {
            LOG.debug("要翻译的原始值集合为空，结束翻译");
            return null;
        }
        LOG.debug("要翻译的原始值集合为: {}", inSet.toString());
        BusinessModel businessModel = ctx.getDirective().getBusinessModelManager().getBusinessModel(businessModelCode);
        Integer destType = getJdbcType(businessModel, destFieldName);
        if (destType != null) {
            if (JDBCType.isText(destType)) {
                Set<Object> tmp = Sets.newHashSet();
                for (Object one : inSet) {
                    tmp.add(one.toString());
                }
                inSet.clear();
                inSet = tmp;
            } else if (JDBCType.isInteger(destType)) {
                Set<Object> tmp = Sets.newHashSet();
                for (Object one : inSet) {
                    tmp.add(NumberUtils.parseNumber(one.toString(), Integer.class));
                }
                inSet.clear();
                inSet = tmp;
            } else if (JDBCType.isDecimal(destType)) {
                Set<Object> tmp = Sets.newHashSet();
                for (Object one : inSet) {
                    tmp.add(NumberUtils.parseNumber(one.toString(), BigDecimal.class));
                }
                inSet.clear();
                inSet = tmp;
            }
        }
        String dataStorageCode = businessModel.getDataStorageCode();
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(destFieldName);
        sql.append(",");
        sql.append(displayFieldName);
        sql.append(" from ");
        SpringJdbcUtils.buildOperateObject(businessModel, sql);
        sql.append(" where ");
        if (Texts.hasText(whereCondition)) {
            sql.append(whereCondition);
            sql.append(" and ");
        }
        sql.append(destFieldName);
        sql.append(" in (:inSet)");
        String query = sql.toString();
        LOG.debug("用于获取翻译值的SQL为: {}", query);
        NamedParameterJdbcTemplate jdbc = ctx.getDirective().getDataStorageManager().getNamedParameterJdbcTemplate(dataStorageCode);
        MapSqlParameterSource msps = new MapSqlParameterSource();
        msps.addValue("inSet", inSet);
        List<Map<String, Object>> mapList = jdbc.queryForList(query, msps);
        inSet.clear();//清理
        Map<String, Object> transMap = Maps.newHashMap();
        for (Map<String, Object> one : mapList) {
            String key = one.get(destFieldName).toString();
            transMap.put(key, one.get(displayFieldName));
            inSetString.remove(key);
        }
        for (String ignore : inSetString) {
            transMap.put(ignore, null);//将数据库查询不到的值，返回null
        }
        inSetString.clear();//清理
        return transMap;
    }

    private Integer getJdbcType(BusinessModel businessModel, String destFieldName) {
        for (BusinessModelField one : businessModel.getFieldSet()) {
            if (one.getFieldName().equalsIgnoreCase(destFieldName)) {
                return one.getJdbcType();
            }
        }
        return null;
    }

    protected String getDisplayFieldName(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "displayColumn");
    }

    protected String getWhereCondition(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "where");
    }

    protected String getDestFieldName(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "destColumn");
    }

    protected String getSrcFieldName(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "srcColumn");
    }

    protected String getBusinessModelCode(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "table");
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return null;
    }
}
