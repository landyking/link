package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelMapSqlParameterSource;
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
import java.util.Map;

/**
 * Created by landy on 2018/7/12.
 */
public class DbExecute implements AbstractExecutionFactory {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DataSourceManager dataSourceManager;

    public String tag() {
        return "dbExecute";
    }

    public AbstractExecution generate(Element element) {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
                final String executionId = element.getAttribute("id");
                final String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                    final Map<String, Object> paramMap = Maps.newHashMap(mojo.getProcessedInputParamMap());
                    final String updateSql = buildExecuteSqlAndParam(mojo, element, paramMap);
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

    protected String buildExecuteSqlAndParam(DirectiveMojo mojo, Element element, Map<String, Object> paramMap) throws LinkException {
        logger.debug("解析要执行的sql语句");
        String sql = element.getTextContent();
        if (Texts.hasText(sql)) {
            sql = BeetlTool.renderBeetl(mojo, sql);
        }
        if (!Texts.hasText(sql)) {
            throw new LinkException("要执行的SQL语句为空");
        }
        return sql;
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
