package com.github.landyking.link.beetl;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.Texts;
import com.github.landyking.link.exception.LinkException;
import com.google.common.base.Throwables;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by landy on 2018/5/11.
 */
public class BeetlTool {
    private static final Logger LOG = LoggerFactory.getLogger(BeetlTool.class);
    private static final GroupTemplate groupTemplate = initGroupTemplate();

    private static GroupTemplate initGroupTemplate() {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            cfg.setErrorHandlerClass("org.beetl.core.ReThrowConsoleErrorHandler");
            cfg.setStrict(true);
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            gt.registerTag("param", ParamTag.class);
            gt.registerTag("trim", TrimTag.class);
            gt.registerTag("if", IfTag.class);
            gt.registerTag("val", ValTag.class);
            return gt;
        } catch (Exception e) {
            LoggerFactory.getLogger(BeetlTool.class).error("构造Beetl表达式处理器失败", e);
            Throwables.propagate(e);
        }
        return null;
    }

    public static GroupTemplate getTemplate() {
        return groupTemplate;
    }

    public static String renderBeetl(DirectiveMojo ctx, String condition) throws LinkException {
        try {
            LOG.debug("过滤条件为表达式，开始计算表达式");
            Template tp = BeetlTool.getTemplate().getTemplate(condition);
            tp.binding("ctx", ctx);
            condition = tp.render();
            LOG.debug("表达式计算结果: {}", condition);
        } catch (Exception e) {
            throw new LinkException("计算Beetl表达式出错:" + e.getMessage(), e);
        }
        return condition;
    }
}
