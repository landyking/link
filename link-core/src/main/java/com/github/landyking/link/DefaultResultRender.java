package com.github.landyking.link;

import com.github.landyking.link.util.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by landy on 2018/7/19.
 */
public class DefaultResultRender implements ResultRender {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void render(DirectiveMojo mojo) {
        if (mojo.getException() != null) {
            logger.error("指令" + mojo.getDirectiveCode() + "执行异常", mojo.getException());
        } else {
            Object extra = mojo.getEndingData();
            if (extra instanceof ExecuteResult) {
                ExecuteResult rst = (ExecuteResult) extra;
                logger.info("影响行数: {}", rst.getEffectCount());
                logger.info("查询总数量: {}", rst.getTotalCount());
                logger.info("主键值: {}", rst.getPrimaryKeyValue());
            }
            Object afterOutput = mojo.getAfterOutput();
            if (afterOutput == null) {
                logger.info("输出处理后数据为null");
            } else if (afterOutput instanceof Collection) {
                logger.info("输出处理后数据列表如下");
                for (Object o : ((Collection) afterOutput)) {
                    logger.info("\t{}", Texts.toStr(o));
                }
            } else {
                logger.info("输出处理后数据为: {}", Texts.toStr(afterOutput));
            }
        }
    }
}
