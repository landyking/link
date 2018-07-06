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
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String directiveCode;

    public DirectiveParser(Resource resource) throws Exception {
        logger.info("start parse file: {}", resource.getURL().toString());
        DefaultDocumentLoader documentLoader = new DefaultDocumentLoader();
        DefaultHandler errorHandler = new DefaultHandler();
        Document doc = documentLoader.loadDocument(new InputSource(resource.getInputStream()), new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (systemId.equals("http://www.github.com/landyking/link/link-1.0.xsd")) {
                    return new InputSource(getClass().getResourceAsStream("/link/link-1.0.xsd"));
                } else {
                    return null;
                }
            }
        }, errorHandler, XmlValidationModeDetector.VALIDATION_XSD, true);
        validateXmlSchema();
    }

    private void validateXmlSchema() {
        logger.info("start validate xml schema");
    }

    public String getDirectiveCode() {
        return directiveCode;
    }

    public void setDirectiveCode(String directiveCode) {
        this.directiveCode = directiveCode;
    }

}
