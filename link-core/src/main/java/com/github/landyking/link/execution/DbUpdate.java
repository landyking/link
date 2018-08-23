package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/12.
 */
public class DbUpdate extends DbExecute {

    public String tag() {
        return "dbUpdate";
    }

    protected String buildExecuteSqlAndParam(DirectiveMojo mojo, Element element, Map<String, Object> paramMap) throws LinkException {
        logger.debug("解析要更新的表");
        String table = mojo.getParser().getSubElement(element, "lk:table").getTextContent();
        logger.debug("解析更新过滤条件");
        String where = mojo.getParser().getSubElement(element, "lk:where").getTextContent();
        logger.debug("解析要更新的字段");
        List<Element> fields = mojo.getParser().getSubElementList(element, "lk:field");
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
                throw new LinkException("节点" + mojo.getParser().getFullPath(f, true, "column") + "处理异常，表达式为" + from, e);
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        String tmpWhere = BeetlTool.renderBeetl(mojo, where);
        if (Texts.hasText(tmpWhere)) {
            sql.append(" where " + tmpWhere);
        }
        return sql.toString();
    }
}
