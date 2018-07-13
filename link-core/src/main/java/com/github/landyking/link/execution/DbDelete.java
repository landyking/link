`package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.LkTools;
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
public class DbDelete implements AbstractExecutionFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DataSourceManager dataSourceManager;

    public String tag() {
        return "dbDelete";
    }

    public AbstractExecution generate(Element element) {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
                System.out.println("DbDelete.execute");
                final String executionId = element.getAttribute("id");
                final String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                    logger.debug("解析要执行删除操作的表");
                    String table = mojo.getParser().getSubElement(element, "lk:table").getTextContent();
                    logger.debug("解析删除过滤条件");
                    String where = mojo.getParser().getSubElement(element, "lk:where").getTextContent();
                    final Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
                    StringBuilder sql = new StringBuilder("delete from ");
                    sql.append(table);
                    final String updateSql = sql.toString() + " where " + BeetlTool.renderBeetl(mojo, where);
                    logger.debug("解析事务配置");
                    String transaction = element.getAttribute("transaction");
                    System.out.println(transaction);
                    logger.debug("组织sql语句和参数");
                    System.out.println(updateSql);
                    System.out.println(paramMap);
                    logger.debug("根据情况开启事务");
                    if (LkTools.isTrue(transaction)) {
                        TransactionTemplate transactionTemplate = dataSourceManager.getTransactionTemplate(dataSourceId, null);
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                doDelete(executionId, dataSourceId, mojo, updateSql);
                            }
                        });
                    } else {
                        doDelete(executionId, dataSourceId, mojo, updateSql);
                    }

                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }
            }
        };
    }

    private void doDelete(String executionId, String dataSourceId, DirectiveMojo mojo, String deleteSql) {
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSourceId);
        Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
        int updateCount = jdbc.update(deleteSql, paramMap);
        ExecuteResult rst = new ExecuteResult();
        rst.setEffectCount(updateCount);
        mojo.setExecuteResult(executionId, rst);
        logger.debug("执行删除结果" + rst);
    }
}