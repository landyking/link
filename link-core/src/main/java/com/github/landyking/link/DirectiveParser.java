package com.github.landyking.link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

/**
 * Created by landy on 2018/7/5.
 * 指令解析器
 */
public class DirectiveParser {
    public static final String LINK_1_0_XSD = "http://www.github.com/landyking/link/link-1.0.xsd";
    public static final String XSD_LOCATION = "/link/link-1.0.xsd";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String directiveCode;
    private final Document document;

    public DirectiveParser(Resource resource) throws Exception {
        logger.info("开始解析文件: {}", resource.getURL().toString());
        DefaultDocumentLoader documentLoader = new DefaultDocumentLoader();
        DefaultHandler errorHandler = new DefaultHandler();
        document = documentLoader.loadDocument(new InputSource(resource.getInputStream()), new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (systemId.equals(LINK_1_0_XSD)) {
                    return new InputSource(getClass().getResourceAsStream(XSD_LOCATION));
                } else {
                    return null;
                }
            }
        }, errorHandler, XmlValidationModeDetector.VALIDATION_XSD, true);
    }

    public String getDirectiveCode() {
        return directiveCode;
    }

    public void setDirectiveCode(String directiveCode) {
        this.directiveCode = directiveCode;
    }

}
