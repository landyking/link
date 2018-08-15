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
                System.out.println("DbUpdate.execute");
                final String executionId = element.getAttribute("id");
                final String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                    logger.debug("解析要更新的表");
                    String table = mojo.getParser().getSubElement(element, "lk:table").getTextContent();
                    logger.debug("解析更新过滤条件");
                    String where = mojo.getParser().getSubElement(element, "lk:where").getTextContent();
                    logger.debug("解析要更新的字段");
                    List<Element> fields = mojo.getParser().getSubElementList(element, "lk:field");
                    final Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
                    StringBuilder sql = new StringBuilder("update ");
                    sql.append(table);
                    sql.append(" set ");
                    for (Element f : fields) {
                        String column = f.getAttribute("column");
                        String from = f.getAttribute("from");
                        if (!Texts.hasText(from)) {
                            //默认from与column相同
                            from = column;
                        }
                        String desc = f.getAttribute("desc");
                        String ignoreNull = f.getAttribute("ignoreNull");
                        String subSql = mojo.getParser().getParamText(f, "subSql");


                        if (!Texts.hasText(subSql)) {
                            if (!paramMap.containsKey(from) && paramMap.get(from) == null) {
                                if (LkTools.isTrue(ignoreNull)) {
                                    //空值忽略不插入
                                    continue;
                                } else {
                                    //要插入该字段，但是入参没有这个参数名字
                                    paramMap.put(from, null);
                                }
                            }
                        }
                        sql.append(column);
                        sql.append("=");
                        if (Texts.hasText(subSql)) {
                            sql.append('(' + subSql + ')');
                        } else {
                            sql.append(":" + from);
                        }
                        sql.append(",");
                    }
                    sql.deleteCharAt(sql.length() - 1);
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
                                try {
                                    doUpdate(executionId, dataSourceId, mojo, updateSql);
                                } catch (LinkException e) {
                                    Throwables.propagate(e);
                                }
                            }
                        });
                    } else {
                        doUpdate(executionId, dataSourceId, mojo, updateSql);
                    }

                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }
            }
        };
    }

    private void doUpdate(String executionId, String dataSourceId, DirectiveMojo mojo, String updateSql) throws LinkException {
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSourceId);
        SpelMapSqlParameterSource paramMap = new SpelMapSqlParameterSource(mojo.getProcessedInputParamMap(), SpelUtils.getSpelPair(mojo));
        int updateCount = jdbc.update(updateSql, paramMap);
        ExecuteResult rst = new ExecuteResult();
        rst.setEffectCount(updateCount);
        mojo.setExecuteResult(executionId, rst);
        logger.debug("执行更新结果" + rst);
    }
}
