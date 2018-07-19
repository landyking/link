package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
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
public class DbInsert implements AbstractExecutionFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DataSourceManager dataSourceManager;

    public String tag() {
        return "dbInsert";
    }

    public AbstractExecution generate(Element element) {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
                System.out.println("DbInsert.execute");
                final String executionId = element.getAttribute("id");
                final String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                    logger.debug("解析要插入的表");
                    Element tableEle = mojo.getParser().getSubElement(element, "lk:table");
                    logger.debug("解析要插入的字段");
                    List<Element> fields = mojo.getParser().getSubElementList(element, "lk:field");
                    final Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
                    StringBuilder sql = new StringBuilder("insert into ");
                    sql.append(tableEle.getTextContent());
                    sql.append(" (");
                    StringBuilder valuesSql = new StringBuilder(") values (");
                    String pkName = null;
                    for (Element f : fields) {
                        String column = f.getAttribute("column");
                        String from = f.getAttribute("from");
                        if (!Texts.hasText(from)) {
                            //默认from与column相同
                            from = column;
                        }
                        String desc = f.getAttribute("desc");
                        String defVal = f.getAttribute("default");
                        String fixed = f.getAttribute("fixed");
                        String ignoreNull = f.getAttribute("ignoreNull");
                        String pk = f.getAttribute("pk");
                        String subSql = mojo.getParser().getParam(f, "subSql");

                        if (Texts.hasText(fixed)) {
                            paramMap.put(from, fixed);
                        }
                        if (!Texts.hasText(subSql)) {
                            if (LkTools.isTrue(pk)) {
                                pkName = from;
                            }
                            if (!paramMap.containsKey(from) && paramMap.get(from) == null) {
                                if (LkTools.isTrue(ignoreNull)) {
                                    //空值忽略不插入
                                    continue;
                                } else {
                                    //要插入该字段，但是入参没有这个参数名字
                                    if (Texts.hasText(defVal)) {
                                        paramMap.put(from, defVal);
                                    } else {
                                        paramMap.put(from, null);
                                    }
                                }
                            }
                        }
                        sql.append(column);
                        if (Texts.hasText(subSql)) {
                            valuesSql.append('(' + subSql + ')');
                        } else {
                            valuesSql.append(":" + from);
                        }
                        sql.append(",");
                        valuesSql.append(",");
                    }
                    sql.deleteCharAt(sql.length() - 1);
                    valuesSql.deleteCharAt(valuesSql.length() - 1);
                    valuesSql.append(")");
                    final String insertSql = sql.toString() + valuesSql.toString();
                    logger.debug("解析事务配置");
                    String transaction = element.getAttribute("transaction");
//                    System.out.println(transaction);
                    logger.debug("组织sql语句和参数");
                    logger.info("插入语句: {}", insertSql);
                    logger.info("参数情况: {}", paramMap);
                    logger.debug("根据情况开启事务");
                    if (LkTools.isTrue(transaction)) {
                        TransactionTemplate transactionTemplate = dataSourceManager.getTransactionTemplate(dataSourceId, null);
                        final String finalPkName = pkName;
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                doInsert(executionId, dataSourceId, mojo, insertSql, finalPkName);
                            }
                        });
                    } else {
                        doInsert(executionId, dataSourceId, mojo, insertSql, pkName);
                    }

                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }
            }
        };
    }

    private void doInsert(String executionId, String dataSourceId, DirectiveMojo mojo, String insertSql, String pkName) {
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSourceId);
        Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
        Object pkValue = null;
        if (Texts.hasText(pkName)) {
            pkValue = paramMap.get(pkName);
        }
        int updateCount;
        if (pkValue != null) {
            updateCount = jdbc.update(insertSql, paramMap);
        } else {
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            updateCount = jdbc.update(insertSql, new MapSqlParameterSource(paramMap), generatedKeyHolder);
            pkValue = generatedKeyHolder.getKey();
        }
        ExecuteResult rst = new ExecuteResult();
        rst.setEffectCount(updateCount);
        rst.setPrimaryKeyValue(pkValue);
        mojo.setExecuteResult(executionId, rst);
        logger.debug("执行插入结果" + rst);
    }
}
