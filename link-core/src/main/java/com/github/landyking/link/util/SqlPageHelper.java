package com.github.landyking.link.util;

import com.github.landyking.link.exception.LinkException;
import com.github.pagehelper.parser.CountSqlParser;

/**
 * Created by landy on 2018/7/19.
 * 分页源码来着mybatis插件 PageHelper。
 * <br/>https://github.com/pagehelper/Mybatis-PageHelper
 */
public class SqlPageHelper {
    static CountSqlParser countSqlParser = new CountSqlParser();

    public static String buildCountSql(DBType dataBaseType, String selectSql) {
        return countSqlParser.getSmartCountSql(selectSql);
    }

    public static String buildPageSelectSql(DBType dataBaseType, String selectSql, Integer pageStart, Integer pageEnd, Integer limit) throws LinkException {
        if (dataBaseType == DBType.ApacheHive) {

        } else if (dataBaseType == DBType.H2Database || dataBaseType == DBType.Mysql) {
            StringBuilder sqlBuilder = new StringBuilder(selectSql.length() + 14);
            sqlBuilder.append(selectSql);
            sqlBuilder.append(" limit ");
            sqlBuilder.append(pageStart);
            sqlBuilder.append(',');
            sqlBuilder.append(limit);
            return sqlBuilder.toString();
        } else if (dataBaseType == DBType.Oracle) {
            StringBuilder sqlBuilder = new StringBuilder(selectSql.length() + 120);
            sqlBuilder.append("select * from ( select tmp_page.*, rownum row_id from ( ");
            sqlBuilder.append(selectSql);
            sqlBuilder.append(" ) tmp_page where rownum <= " + pageEnd + " ) where row_id > " + pageStart);
            return sqlBuilder.toString();
        }
        throw new LinkException("不支持的数据库类型: " + dataBaseType);
    }
}
