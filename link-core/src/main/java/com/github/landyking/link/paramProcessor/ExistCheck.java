package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.*;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.Texts;
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
        if (localDict != null) {
            String dictName = localDict.getAttribute("name");
            LocalDict dict = localDictManager.getLocalDict(dictName);
            String paramDesc = param.getAttribute("name") + ":" + mojo.getParser().getParam(param, "desc");
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
        }
        return null;
    }
}
