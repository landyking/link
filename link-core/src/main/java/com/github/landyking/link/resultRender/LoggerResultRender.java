package com.github.landyking.link.resultRender;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ExecuteResult;
import com.github.landyking.link.ResultRender;
import com.github.landyking.link.util.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Created by landy on 2018/7/19.
 */
public class LoggerResultRender implements ResultRender {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void render(DirectiveMojo mojo) {
        if (mojo.getException() != null) {
            logger.error("指令" + mojo.getDirectiveCode() + "执行异常", mojo.getException());
        } else {
            Map<String, ExecuteResult> endingData = mojo.getEndingData();
            for (String one : endingData.keySet()) {
                ExecuteResult rst = endingData.get(one);
                logger.info("{} 影响行数: {}", one, rst.getEffectCount());
                logger.info("{} 查询总数量: {}", one, rst.getTotalCount());
                logger.info("{} 主键值: {}", one, rst.getPrimaryKeyValue());
            }
            Object afterOutput = mojo.getAfterOutput();
            if (afterOutput == null) {
                logger.info("输出处理后数据为null");
            } else {
                logger.info("输出处理后数据为: {}", Texts.toStr(afterOutput));
            }
        }
    }
}
