package com.github.landyking.link.execution;

import com.github.landyking.link.*;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.DBType;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.SqlPageHelper;
import com.github.landyking.link.util.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/12.
 */
public class DbSelect implements AbstractExecutionFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DataSourceManager dataSourceManager;

    public String tag() {
        return "dbSelect";
    }

    public AbstractExecution generate(Element element) {
        return new AbstractExecution(element) {

            @Override
            public void execute(final DirectiveMojo mojo) throws LinkException {
//                System.out.println("DbSelect.execute");
                final String executionId = element.getAttribute("id");
                final String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                    StringBuilder sql = new StringBuilder("select ");

                    logger.debug("解析要查询的字段");
                    processFields(mojo, sql);
                    logger.debug("解析要查询的表");
                    processTableAndJoin(mojo, sql);
                    logger.debug("解析where");
                    Element whereEle = mojo.getParser().getSubElement(element, "lk:where");
                    if (whereEle != null) {
                        sql.append("where ");
                        sql.append(BeetlTool.renderBeetl(mojo, whereEle.getTextContent()) + " ");
                    }
                    logger.debug("解析other");
                    Element otherEle = mojo.getParser().getSubElement(element, "lk:other");
                    if (otherEle != null) {
                        sql.append(BeetlTool.renderBeetl(mojo, otherEle.getTextContent()) + " ");
                    }
                    final Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
                    String selectSql = sql.toString();
                    logger.debug("解析分页参数");
                    String pagination = element.getAttribute("pagination");
                    boolean needPage = LkTools.isTrue(pagination);
                    String countSql = "";
                    if (needPage) {
                        boolean countQuery = LkTools.isTrue(element.getAttribute("countQuery"));
                        String pageParamName = element.getAttribute("pageParamName");
                        String pageStartParamName = element.getAttribute("pageStartParamName");
                        String limitParamName = element.getAttribute("limitParamName");
                        int limitDefaultSize = Integer.parseInt(element.getAttribute("limitDefaultSize"));

                        Integer pageStart, pageEnd;
                        Integer page = getParamPage(mojo, pageParamName);
                        Integer limit = getParamLimit(mojo, limitParamName, limitDefaultSize);
                        pageStart = (page - 1) * limit;
                        //直接提供pageStart，覆盖之前计算的
                        pageStart = getParamPageStart(mojo, pageStartParamName, pageStart);
                        pageEnd = pageStart + limit;
                        DBType dataBaseType = dataSourceManager.getDataBaseType(dataSourceId);
                        if (countQuery) {
                            countSql = SqlPageHelper.buildCountSql(dataBaseType, selectSql);
                        }
//                        logger.debug("自动分页前sql: {}", selectSql);
                        selectSql = SqlPageHelper.buildPageSelectSql(dataBaseType, selectSql, pageStart, pageEnd, limit);
                    }
                    logger.debug("组织sql语句和参数");
                    if (Texts.hasText(countSql)) {
                        logger.info("数量查询sql: {}", countSql);
                    }
                    logger.info("查询sql: {}", selectSql);
                    logger.info(paramMap.toString());
                    logger.debug("解析事务配置");
                    String transaction = element.getAttribute("transaction");
                    logger.debug("根据情况开启事务");
                    if (LkTools.isTrue(transaction)) {
                        TransactionTemplate transactionTemplate = dataSourceManager.getTransactionTemplate(dataSourceId, null);
                        final String finalSelectSql = selectSql;
                        final String finalCountSql = countSql;
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                doSelect(executionId, dataSourceId, mojo, finalSelectSql, finalCountSql);
                            }
                        });
                    } else {
                        doSelect(executionId, dataSourceId, mojo, selectSql, countSql);
                    }

                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }
            }

            private Integer getParamPageStart(DirectiveMojo mojo, String pageStartParamName, Integer defVal) {
                String str = mojo.getPot().getInputParamText(pageStartParamName);
                if (Texts.hasText(str)) {
                    try {
                        int i = Integer.parseInt(str);
                        if (i >= 0) {
                            return i;
                        }
                    } catch (Exception e) {
                        //nothing
                    }
                }
                return defVal;
            }

            private Integer getParamLimit(DirectiveMojo mojo, String limitParamName, int limitDefaultSize) {
                String str = mojo.getPot().getInputParamText(limitParamName);
                if (Texts.hasText(str)) {
                    try {
                        int i = Integer.parseInt(str);
                        if (i > 0) {
                            return i;
                        }
                    } catch (Exception e) {
                        //nothing
                    }
                }
                return limitDefaultSize;
            }

            private Integer getParamPage(DirectiveMojo mojo, String pageParamName) {
                String str = mojo.getPot().getInputParamText(pageParamName);
                if (Texts.hasText(str)) {
                    try {
                        int i = Integer.parseInt(str);
                        if (i >= 1) {
                            return i;
                        }
                    } catch (Exception e) {
                        //nothing
                    }
                }
                return 1;
            }

            private void processTableAndJoin(DirectiveMojo mojo, StringBuilder sql) throws LinkException {
                Element baseTable = mojo.getParser().getSubElement(element, "lk:from/lk:base");
                String basePrefix = baseTable.getAttribute("prefix");
                String baseTableName = baseTable.getAttribute("table");
                String baseAlias = baseTable.getAttribute("alias");
                sql.append(" from ");
                if (Texts.hasText(basePrefix)) {
                    sql.append(basePrefix);
                    sql.append(".");
                }
                sql.append(baseTableName + " ");
                if (Texts.hasText(baseAlias)) {
                    sql.append(baseAlias + " ");
                }
                List<Element> joinList = mojo.getParser().getSubElementList(element, "lk:from/lk:join");
                for (Element one : joinList) {
                    String type = one.getAttribute("type");
                    String prefix = one.getAttribute("prefix");
                    String table = one.getAttribute("table");
                    String alias = one.getAttribute("alias");
                    String onCondition = mojo.getParser().getSubElement(one, "lk:on").getTextContent();
                    sql.append(type);
                    sql.append(" join ");
                    if (Texts.hasText(prefix)) {
                        sql.append(prefix);
                        sql.append(".");
                    }
                    sql.append(table + " ");
                    if (Texts.hasText(alias)) {
                        sql.append(alias + " ");
                    }
                    sql.append("on ");
                    sql.append(onCondition + " ");
                }
            }

            private void processFields(DirectiveMojo mojo, StringBuilder sql) throws LinkException {
                List<Element> fields = mojo.getParser().getSubElementList(element, "lk:field");
                for (Element f : fields) {
                    String column = f.getAttribute("column");
                    String columnAlias = f.getAttribute("columnAlias");
                    String tableAlias = f.getAttribute("tableAlias");
                    String desc = f.getAttribute("desc");
                    if (Texts.hasText(tableAlias)) {
                        sql.append(tableAlias);
                        sql.append(".");
                    }
                    sql.append(column);
                    if (Texts.hasText(columnAlias)) {
                        sql.append(" as ");
                        sql.append(columnAlias);
                    }
                    sql.append(",");
                }
                sql.deleteCharAt(sql.length() - 1);//删除最后的逗号
            }
        };
    }


    private void doSelect(String executionId, String dataSourceId, DirectiveMojo mojo, String selectSql, String countSql) {
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSourceId);
        Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
        int total;
        List<Map<String, Object>> dataList = Collections.emptyList();
        boolean countQuery = Texts.hasText(countSql);
        if (countQuery) {
            total = jdbc.queryForObject(countSql, paramMap, Number.class).intValue();
            if (total > 0) {
                dataList = jdbc.queryForList(selectSql, paramMap);
            }
        } else {
            dataList = jdbc.queryForList(selectSql, paramMap);
            total = dataList.size();
        }

        ExecuteResult rst = new ExecuteResult();
        rst.setTotalCount(total);
        rst.setData(dataList);
        mojo.setExecuteResult(executionId, rst);
        if (countQuery) {
            logger.debug("执行查询结果，总数量{}条,查出数据{}条", total, dataList.size());
        } else {
            logger.debug("执行查询结果，查出数据{}条", dataList.size());
        }

    }
}
