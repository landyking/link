package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by landy on 2018/7/12.
 */
public class DbSelect extends DbQuery {

    public String tag() {
        return "dbQuery";
    }

    protected String buildQuerySql(DirectiveMojo mojo, Element element) throws LinkException {
        StringBuilder sql = new StringBuilder("select ");

        logger.debug("解析要查询的字段");
        processFields(mojo, element, sql);
        logger.debug("解析要查询的表");
        processTableAndJoin(mojo, element, sql);
        logger.debug("解析where");
        Element whereEle = mojo.getParser().getSubElement(element, "lk:where");
        if (whereEle != null) {
            String tmp = BeetlTool.renderBeetl(mojo, whereEle.getTextContent());
            if (Texts.hasText(tmp)) {
                sql.append("where ");
                sql.append(tmp + " ");
            }
        }
        logger.debug("解析other");
        Element otherEle = mojo.getParser().getSubElement(element, "lk:other");
        if (otherEle != null) {
            sql.append(BeetlTool.renderBeetl(mojo, otherEle.getTextContent()) + " ");
        }
        return sql.toString();
    }


    private void processTableAndJoin(DirectiveMojo mojo, Element element, StringBuilder sql) throws LinkException {
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

    private void processFields(DirectiveMojo mojo, Element element, StringBuilder sql) throws LinkException {
        List<Element> fields = mojo.getParser().getSubElementList(element, "lk:field");
        for (Element f : fields) {
            String column = f.getAttribute("column");
            String columnAlias = f.getAttribute("columnAlias");
            String tableAlias = f.getAttribute("tableAlias");
            String desc = f.getAttribute("desc");
            String subSql = mojo.getParser().getParamText(f, "subSql");
            if (Texts.hasText(subSql)) {
                sql.append('(');
                sql.append(subSql);
                sql.append(')');
                sql.append(' ');
                String alias = null;
                if (Texts.hasText(column)) {
                    alias = column;
                }
                if (Texts.hasText(columnAlias)) {
                    alias = columnAlias;
                }
                Assert.hasText(alias, "子查询别名为空");
                sql.append(" as ");
                sql.append(alias);
            } else {
                if (Texts.hasText(tableAlias)) {
                    sql.append(tableAlias);
                    sql.append(".");
                }
                sql.append(column);
                if (Texts.hasText(columnAlias)) {
                    sql.append(" as ");
                    sql.append(columnAlias);
                }
            }
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);//删除最后的逗号
    }
}
