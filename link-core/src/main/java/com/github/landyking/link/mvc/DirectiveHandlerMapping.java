package com.github.landyking.link.mvc;

import com.github.landyking.link.DirectiveParser;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by landy on 2018/9/24.
 */
public class DirectiveHandlerMapping extends AbstractHandlerMapping {

    public DirectiveHandlerMapping() {
        this.setAlwaysUseFullPath(true);
    }

    private String basePackage = "";

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        if (StringUtils.hasText(this.basePackage)) {
            if (this.basePackage.endsWith(".")) {
                this.basePackage = this.basePackage.substring(0, this.basePackage.length() - 1);
            }
        } else {
            this.basePackage = "";
        }
    }

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        Object handler = lookupHandler(lookupPath, request);
        if (handler != null && logger.isDebugEnabled()) {
            logger.debug("Mapping [" + lookupPath + "] to " + handler);
        } else if (handler == null && logger.isTraceEnabled()) {
            logger.trace("No handler mapping found for [" + lookupPath + "]");
        }
        return handler;
    }

    protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
        String tmp = this.basePackage + urlPath;
        tmp = tmp.replace('.', '/');
        while (tmp.endsWith("/")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        String location = "classpath:" + tmp + ".xml";
        Resource resource = getApplicationContext().getResource(location);
        if (resource.exists() && resource.isReadable()) {
            DirectiveParser parser = new DirectiveParser(resource, getApplicationContext());
            parser.setDirectiveCode(urlPath);
            return parser;
        } else {
            logger.info("Can't found directive file on location: " + resource.toString());
            // No handler found...
            return null;
        }
    }


}
