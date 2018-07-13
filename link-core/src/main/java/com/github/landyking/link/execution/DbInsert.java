package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.exception.LinkException;
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
                        String subSql = mojo.getParser().getParam(f, "subSql");
                        if (Texts.hasText(fixed)) {
                            paramMap.put(from, fixed);
                        }
                        if (!Texts.hasText(subSql)) {
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
                    System.out.println(transaction);
                    logger.debug("组织sql语句和参数");
                    System.out.println(insertSql);
                    System.out.println(paramMap);
                    logger.debug("根据情况开启事务");
                    if (LkTools.isTrue(transaction)) {
                        TransactionTemplate transactionTemplate = dataSourceManager.getTransactionTemplate(dataSourceId, null);
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                doInsert(dataSourceId, mojo, insertSql);
                            }
                        });
                    } else {
                        doInsert(dataSourceId, mojo, insertSql);
                    }

                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }
            }
        };
    }

    private void doInsert(String dataSourceId, DirectiveMojo mojo, String insertSql) {
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSourceId);
        Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
        int updateCount = jdbc.update(insertSql, paramMap);
        logger.debug("执行插入" + updateCount);
    }
}
