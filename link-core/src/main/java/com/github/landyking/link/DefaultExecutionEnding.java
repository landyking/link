package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;

import java.util.Map;

/**
 * Created by landy on 2018/7/19.
 */
public class DefaultExecutionEnding implements ExecutionEnding {
    @Override
    public ExecutionEndingData process(DirectiveMojo mojo) throws LinkException {
        Map<String, ExecuteResult> erm = mojo.getExecuteResultMap();
        ExecuteResult def = erm.get("default");
        if (def != null) {
            return def;
        } else {
            throw new LinkException("不支持的结果数据");
        }
    }
}
