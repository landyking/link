package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DataSourceManager;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ValueBag;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
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
    private DataSourceManager dataSourceManager;

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void processOutput(Element config, Element param, DirectiveMojo ctx, String name, List<Map<String, ValueBag>> outList) throws Exception {
        String tableName = getTableName(ctx, config);
        String srcFieldName = getSrcFieldName(ctx, config);
        if (!Texts.hasText(srcFieldName)) {
            srcFieldName = Texts.firstHasText(param.getAttribute("from"), name);
        }
        String destFieldName = getDestFieldName(ctx, config);
        String whereCondition = getWhereCondition(ctx, config);
        String displayFieldName = getDisplayFieldName(ctx, config);
        Boolean failUseOriginal = getFailUseOriginal(ctx, config);
        String cacheName = getCacheName(tableName, destFieldName, displayFieldName, whereCondition);

        boolean containsCache = cacheManager.getCacheNames().contains(cacheName);
        Map<String, Object> transMap = null;
        boolean allMatch = true;
        if (containsCache) {
            //缓存存在
            Cache cache = cacheManager.getCache(cacheName);
            transMap = Maps.newHashMap();
            //检查要翻译的项是否都能匹配
            for (Map<String, ValueBag> one : outList) {
                ValueBag srcItem = one.get(srcFieldName);
                Object ov = srcItem.getFinalValue();
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
                transMap = makeTransMap(ctx, config, outList, tableName, srcFieldName, destFieldName, whereCondition, displayFieldName);
            } else {
                LOG.debug("使用缓存{}进行翻译", cacheName);
            }
        } else {
            //缓存不存在，从数据库加载
            transMap = makeTransMap(ctx, config, outList, tableName, srcFieldName, destFieldName, whereCondition, displayFieldName);
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
        for (Map<String, ValueBag> one : outList) {
            ValueBag srcItem = one.get(srcFieldName);
            ValueBag outItem = one.get(name);
            Object ov = srcItem.getFinalValue();
            if (ov != null) {
                Object out = transMap.get(ov.toString());
                if (out != null) {
                    outItem.setModifyValue(out);
                } else {
                    if (failUseOriginal) {
                        outItem.setModifyValue(ov);
                    } else {
                        outItem.setModifyValue(null);
                    }
                }
            }
        }
    }

    private Boolean getFailUseOriginal(DirectiveMojo mojo, Element config) {
        return LkTools.isTrue(mojo.getParser().getParam(config, "failUseOriginal"));
    }

    protected String getCacheName(String tableName, String destFieldName, String displayFieldName, String whereCondition) {
        String name = tableName + SPLIT + destFieldName + SPLIT + displayFieldName;
        if (Texts.hasText(whereCondition)) {
            name += SPLIT + Hashing.md5().hashString(whereCondition, Charsets.UTF_8).toString();
        }
        return name;
    }

    protected Map<String, Object> makeTransMap(DirectiveMojo ctx, Element config, List<Map<String, ValueBag>> rstList, String tableName, String srcFieldName, String destFieldName, String whereCondition, String displayFieldName) throws LinkException {
        Set<Object> inSet = Sets.newHashSet();
        Set<String> inSetString = Sets.newHashSet();
        for (int i = 0; i < rstList.size(); i++) {
            Map<String, ValueBag> one = rstList.get(i);
            ValueBag item = one.get(srcFieldName);
            Assert.notNull(item, "参数" + srcFieldName + "对应结果项为空");
            if (item.getFinalValue() != null) {
                inSet.add(item.getFinalValue());
                inSetString.add(Texts.toStr(item.getFinalValue()));
            }
        }

        if (inSet.isEmpty()) {
            LOG.debug("要翻译的原始值集合为空，结束翻译");
            return null;
        }
        LOG.debug("要翻译的原始值集合为: {}", inSet.toString());
        String destType = getDestFileType(ctx, config);
        if (destType != null) {
            if (Texts.isSame("string", destType)) {
                Set<Object> tmp = Sets.newHashSet();
                for (Object one : inSet) {
                    tmp.add(one.toString());
                }
                inSet.clear();
                inSet = tmp;
            } else if (Texts.isSame("int", destType)) {
                Set<Object> tmp = Sets.newHashSet();
                for (Object one : inSet) {
                    tmp.add(NumberUtils.parseNumber(one.toString(), Integer.class));
                }
                inSet.clear();
                inSet = tmp;
            } else if (Texts.isSame("decimal", destType)) {
                Set<Object> tmp = Sets.newHashSet();
                for (Object one : inSet) {
                    tmp.add(NumberUtils.parseNumber(one.toString(), BigDecimal.class));
                }
                inSet.clear();
                inSet = tmp;
            }
        }
        String queryDataSource = getQueryDataSource(ctx, config);
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(destFieldName);
        sql.append(",");
        sql.append(displayFieldName);
        sql.append(" from ");
        sql.append(tableName);
        sql.append(" where ");
        if (Texts.hasText(whereCondition)) {
            sql.append(whereCondition);
            sql.append(" and ");
        }
        sql.append(destFieldName);
        sql.append(" in (:inSet)");
        String query = sql.toString();
        LOG.debug("用于获取翻译值的SQL为: {}", query);
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(queryDataSource);
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

    private String getQueryDataSource(DirectiveMojo ctx, Element config) {
        return ctx.getParser().getParam(config, "queryDataSource");
    }

    private String getDestFileType(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "destFieldType");
    }


    protected String getDisplayFieldName(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "displayField");
    }

    protected String getWhereCondition(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "where");
    }

    protected String getDestFieldName(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "destField");
    }

    protected String getSrcFieldName(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "srcField");
    }

    protected String getTableName(DirectiveMojo mojo, Element config) {
        return mojo.getParser().getParam(config, "table");
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        return null;
    }
}
