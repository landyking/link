package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelMapSqlParameterSource;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/12.
 */
public class DbUpdate implements AbstractExecutionFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DataSourceManager dataSourceManager;

    public String tag() {
        return "dbUpdate";
    }

    public AbstractExecution generate(Element element) {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
                final String executionId = element.getAttribute("id");
                final String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                    logger.debug("解析要更新的表");
                    String table = mojo.getParser().getSubElement(element, "lk:table").getTextContent();
                    logger.debug("解析更新过滤条件");
                    String where = mojo.getParser().getSubElement(element, "lk:where").getTextContent();
                    logger.debug("解析要更新的字段");
                    List<Element> fields = mojo.getParser().getSubElementList(element, "lk:field");
                    final Map<String, Object> paramMap = Maps.newHashMap();
                    StringBuilder sql = new StringBuilder("update ");
                    sql.append(table);
                    sql.append(" set ");
                    SpelPair sp = SpelUtils.getSpelPair(mojo);
                    for (Element f : fields) {
                        String column = f.getAttribute("column");
                        String from = f.getAttribute("from");
                        if (!Texts.hasText(from)) {
                            //默认from与column相同
                            from = "#root[input][" + column + "]";
                        }
                        try {
                            String desc = f.getAttribute("desc");
                            String ignoreNull = f.getAttribute("ignoreNull");
                            String subSql = mojo.getParser().getParamText(f, "subSql");

                            if (!Texts.hasText(subSql)) {
                                Object value = sp.getExp().parseExpression(from).getValue(sp.getCtx());
                                if (value == null && LkTools.isTrue(ignoreNull)) {
                                    continue;
                                }
                                paramMap.put(column, value);
                            }
                            sql.append(column);
                            sql.append("=");
                            if (Texts.hasText(subSql)) {
                                sql.append('(' + subSql + ')');
                            } else {
                                sql.append(":" + column);
                            }
                            sql.append(",");
                        } catch (Exception e) {
                            throw new LinkException("节点" + mojo.getParser().getFullPath(f, true) + "处理异常，表达式为" + from, e);
                        }
                    }
                    sql.deleteCharAt(sql.length() - 1);
                    final String updateSql = sql.toString() + " where " + BeetlTool.renderBeetl(mojo, where);
                    logger.debug("解析事务配置");
                    String transaction = element.getAttribute("transaction");
                    logger.info("更新SQL语句: {}", updateSql);
                    logger.info("更新参数: {}", paramMap);
                    logger.debug("根据情况开启事务");
                    if (LkTools.isTrue(transaction)) {
                        TransactionTemplate transactionTemplate = dataSourceManager.getTransactionTemplate(dataSourceId, null);
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                try {
                                    doUpdate(executionId, dataSourceId, mojo, updateSql, paramMap);
                                } catch (LinkException e) {
                                    Throwables.propagate(e);
                                }
                            }
                        });
                    } else {
                        doUpdate(executionId, dataSourceId, mojo, updateSql, paramMap);
                    }

                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }
            }
        };
    }

    private void doUpdate(String executionId, String dataSourceId, DirectiveMojo mojo, String updateSql, Map<String, Object> pm) throws LinkException {
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSourceId);
        SpelMapSqlParameterSource paramMap = new SpelMapSqlParameterSource(pm, SpelUtils.getSpelPair(mojo));
        int updateCount = jdbc.update(updateSql, paramMap);
        ExecuteResult rst = new ExecuteResult();
        rst.setEffectCount(updateCount);
        mojo.setExecuteResult(executionId, rst);
        logger.debug("执行更新结果" + rst);
    }
}
