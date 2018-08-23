package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelMapSqlParameterSource;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Element;

import javax.annotation.Resource;

public class DbSql implements AbstractExecutionFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DataSourceManager dataSourceManager;

    public String tag() {
        return "dbSql";
    }

    public AbstractExecution generate(Element element) {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
                final String executionId = element.getAttribute("id");
                final String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                    logger.debug("解析要执行的sql语句");
                    String sql = element.getTextContent();
                    if (Texts.hasText(sql)) {
                        sql = BeetlTool.renderBeetl(mojo, sql);
                    }
                    if (!Texts.hasText(sql)) {
                        throw new LinkException("要执行的SQL语句为空");
                    }
                    boolean isQuery = isQuerySql(element, sql);
                    if (isQuery) {
                        logger.debug("判断SQL语句类型为: 查询");
                        logger.debug("解析分页参数");


                    } else {
                        logger.debug("判断SQL语句类型为: 更新");

                    }


                    logger.debug("解析事务配置");
                    String transaction = element.getAttribute("transaction");
                    logger.debug("根据情况开启事务");


                    String table = mojo.getParser().getSubElement(element, "lk:table").getTextContent();
                    logger.debug("解析删除过滤条件");
                    String where = mojo.getParser().getSubElement(element, "lk:where").getTextContent();
                    StringBuilder sql = new StringBuilder("delete from ");
                    sql.append(table);
                    String tmpWhere = BeetlTool.renderBeetl(mojo, where);
                    if (Texts.hasText(tmpWhere)) {
                        sql.append(" where " + tmpWhere);
                    }
                    final String updateSql = sql.toString();
                    logger.debug("解析事务配置");
                    String transaction = element.getAttribute("transaction");
//                    System.out.println(transaction);
                    logger.info("删除sql语句: {}", updateSql);
                    logger.debug("根据情况开启事务");
                    if (LkTools.isTrue(transaction)) {
                        TransactionTemplate transactionTemplate = dataSourceManager.getTransactionTemplate(dataSourceId, null);
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                try {
                                    doDelete(executionId, dataSourceId, mojo, updateSql);
                                } catch (LinkException e) {
                                    Throwables.propagate(e);
                                }
                            }
                        });
                    } else {
                        doDelete(executionId, dataSourceId, mojo, updateSql);
                    }

                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }
            }

            private boolean isQuerySql(Element element, String sql) {
                return sql.trim().substring(0, 5).equalsIgnoreCase("select");
            }
        };
    }

    private void doDelete(String executionId, String dataSourceId, DirectiveMojo mojo, String deleteSql) throws LinkException {
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSourceId);
        SpelMapSqlParameterSource paramMap = new SpelMapSqlParameterSource(mojo.getProcessedInputParamMap(), SpelUtils.getSpelPair(mojo));
        int updateCount = jdbc.update(deleteSql, paramMap);
        ExecuteResult rst = new ExecuteResult();
        rst.setEffectCount(updateCount);
        mojo.setExecuteResult(executionId, rst);
        logger.debug("执行删除结果" + rst);
    }
}
