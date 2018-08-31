package com.github.landyking.link.paramProcessor;

import com.github.landyking.link.AbstractParamProcessor;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.LocalDictManager;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.util.LkTools;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: landy
 * @date: 2018-07-22 20:02
 */
public class DictTranslator extends AbstractParamProcessor {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    private LocalDictManager localDictManager;


    @Override
    public void processOutput(Element config, Element param, DirectiveMojo ctx, String name, List<Map<String, Object>> outList) throws Exception {
        String dictName = getDictName(ctx, config);
        String srcFieldName = getSrcFieldName(ctx, config);
//        if (!Texts.hasText(srcFieldName)) {
        srcFieldName = name;//fixme 同QueryTranslator
//        }
//        String whereCondition = getWhereCondition(ctx, config);
        Boolean failUseOriginal = getFailUseOriginal(ctx, config);

        //使用最终的map进行翻译
        for (Map<String, Object> one : outList) {
            Object ov = one.get(srcFieldName);
            if (ov != null) {
                Object out = localDictManager.translate(dictName, ov.toString());
                if (out != null) {
                    one.put(name, out);
                } else {
                    if (failUseOriginal) {
                        one.put(name, ov);
                    } else {
                        one.put(name, "");
                    }
                }
            }
        }
    }

    private Boolean getFailUseOriginal(DirectiveMojo mojo, Element config) throws LinkException {
        return LkTools.isTrue(mojo.getParser().getParamText(config, "failUseOriginal"));
    }


   /* protected String getWhereCondition(DirectiveMojo mojo, Element config) throws LinkException {
        return mojo.getParser().getParamText(config, "where");
    }*/


    protected String getSrcFieldName(DirectiveMojo mojo, Element config) throws LinkException {
        return mojo.getParser().getParamText(config, "srcField");
    }

    protected String getDictName(DirectiveMojo mojo, Element config) throws LinkException {
        return mojo.getParser().getParamText(config, "dict");
    }

    @Override
    public Object processInput(Element config, Element param, DirectiveMojo mojo, Object in) throws Exception {
        String name = param.getAttribute("name");
        Map<String, Object> vals = Maps.newHashMap();
        vals.put(name, in);
        processOutput(config, param, mojo, name, Arrays.asList(vals));
        return vals.get(name);
    }

    public void setLocalDictManager(LocalDictManager localDictManager) {
        this.localDictManager = localDictManager;
    }
}
