package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DataSourceManager;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.beetl.BeetlTool;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelMapSqlParameterSource;
import com.github.landyking.link.spel.SpelUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/8/1.
 */
public class DuplicateCheck extends AbstractParamProcessor {
    private DataSourceManager dataSourceManager;

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }


    @Override
    public void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, Object>> outList) throws Exception {
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        String name = param.getAttribute("name");
        String paramDesc = name + ":" + mojo.getParser().getParam(param, "desc");
        Element countQuery = mojo.getParser().getSubElement(config, "lk:countQuery");
        Assert.notNull(countQuery, "入参[" + paramDesc + "]处理器DuplicateCheck中的countQuery为空");
        String dataSource = countQuery.getAttribute("dataSource");
        String textContent = countQuery.getTextContent();
        final SpelMapSqlParameterSource paramMap = new SpelMapSqlParameterSource(mojo.getProcessedInputParamMap(), SpelUtils.getSpelPair(mojo));
        if (!paramMap.hasValue("this")) {
            paramMap.addValue("this", in);
        }
        NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSource);
        String sql = BeetlTool.renderBeetl(mojo, textContent);
        int count = jdbc.queryForObject(sql, paramMap, Number.class).intValue();
        if (count > 0) {
            throw new LinkException("处理入参[" + paramDesc + "]异常，指定信息已存在" + count + "条");
        }
        return in;
    }
}
