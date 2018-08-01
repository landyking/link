package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.*;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * @author: landy
 * @date: 2018-07-30 23:20
 */
public class ExistCheck extends AbstractParamProcessor {
    private DataSourceManager dataSourceManager;
    private LocalDictManager localDictManager;

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public void setLocalDictManager(LocalDictManager localDictManager) {
        this.localDictManager = localDictManager;
    }

    @Override
    public void processOutput(Element config, Element param, DirectiveMojo mojo, String name, List<Map<String, ValueBag>> outList) throws Exception {

    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        Element localDict = mojo.getParser().getSubElement(config, "lk:localDict");
        String name = param.getAttribute("name");
        String paramDesc = name + ":" + mojo.getParser().getParam(param, "desc");
        if (localDict != null) {
            String dictName = localDict.getAttribute("name");
            LocalDict dict = localDictManager.getLocalDict(dictName);

            if (dict != null) {
                String val = Texts.toStr(in);
                if (dict.getItems().containsKey(val)) {
                    return in;
                } else {
                    throw new LinkException("处理入参[" + paramDesc + "]异常，字典" + dictName + "中不存在项" + val);
                }
            } else {
                throw new LinkException("处理入参[" + paramDesc + "]异常，字典" + dictName + "不存在");
            }
        } else {
            Element countQuery = mojo.getParser().getSubElement(config, "lk:countQuery");
            Assert.notNull(countQuery, "入参[" + paramDesc + "]处理器ExistCheck中的countQuery为空");
            String dataSource = countQuery.getAttribute("dataSource");
            String textContent = countQuery.getTextContent();
            final Map<String, Object> paramMap = mojo.getProcessedInputParamMap();
            if (!paramMap.containsKey("this")) {
                paramMap.put("this", in);
            }
            NamedParameterJdbcTemplate jdbc = dataSourceManager.getNamedParameterJdbcTemplate(dataSource);
            int count = jdbc.queryForObject(textContent, paramMap, Number.class).intValue();
            if (count <= 0) {
                throw new LinkException("处理入参[" + paramDesc + "]异常，查询不到指定信息");
            }
        }
        return in;
    }
}
