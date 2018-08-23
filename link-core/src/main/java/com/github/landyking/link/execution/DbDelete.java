package com.github.landyking.link.execution;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by landy on 2018/7/12.
 */
public class DbDelete extends DbExecute {
    public String tag() {
        return "dbDelete";
    }

    @Override
    protected String buildExecuteSqlAndParam(DirectiveMojo mojo, Element element, Map<String, Object> paramMap) throws LinkException {
        logger.debug("解析要执行删除操作的表");
        String table = mojo.getParser().getSubElement(element, "lk:table").getTextContent();
        logger.debug("解析删除过滤条件");
        String where = mojo.getParser().getSubElement(element, "lk:where").getTextContent();
        StringBuilder sql = new StringBuilder("delete from ");
        sql.append(table);
        String tmpWhere = BeetlTool.renderBeetl(mojo, where);
        if (Texts.hasText(tmpWhere)) {
            sql.append(" where " + tmpWhere);
        }
        return sql.toString();
    }
}
