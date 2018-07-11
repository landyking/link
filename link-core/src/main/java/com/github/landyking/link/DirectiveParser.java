package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by landy on 2018/7/5.
 * 指令解析器
 */
public class DirectiveParser {
    public static final String LINK_1_0_XSD = "http://www.github.com/landyking/link/link-1.0.xsd";
    public static final String XSD_LOCATION = "/link/link-1.0.xsd";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final XPath xPath = initXPath();

    private XPath initXPath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                if (prefix == null) throw new NullPointerException("Null prefix");
                else if ("lk".equals(prefix)) return "http://www.github.com/landyking/link";
                else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
                return XMLConstants.NULL_NS_URI;
            }

            public String getPrefix(String uri) {
                throw new UnsupportedOperationException();
            }

            public Iterator getPrefixes(String uri) {
                throw new UnsupportedOperationException();
            }

        });
        return xPath;
    }

    private String directiveCode;
    private final Document document;

    public DirectiveParser(Resource resource) throws Exception {
        logger.info("开始解析文件: {}", resource.getURL().toString());
//        DefaultDocumentLoader documentLoader = new DefaultDocumentLoader();
        DefaultHandler errorHandler = new DefaultHandler();
        EntityResolver entityResolver = new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (systemId.equals(LINK_1_0_XSD)) {
                    return new InputSource(getClass().getResourceAsStream(XSD_LOCATION));
                } else {
                    return null;
                }
            }
        };
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        documentBuilder.setEntityResolver(entityResolver);
        documentBuilder.setErrorHandler(errorHandler);
//        document = documentLoader.loadDocument(new InputSource(resource.getInputStream()), entityResolver, errorHandler, XmlValidationModeDetector.VALIDATION_XSD, true);
        document = documentBuilder.parse(resource.getURL().toString());
    }

    public String getDirectiveCode() {
        return directiveCode;
    }

    public void setDirectiveCode(String directiveCode) {
        this.directiveCode = directiveCode;
    }

    public List<Element> getInputParamList() throws LinkException {
        try {
            NodeList params = (NodeList) xPath.evaluate("/lk:directive/lk:input/lk:param", document, XPathConstants.NODESET);
            List<Element> rst = nodeList2ElementList(params);
            return rst;
        } catch (XPathExpressionException e) {
            throw new LinkException("parse input param failure", e);
        }
    }

    private List<Element> nodeList2ElementList(NodeList params) {
        List<Element> rst = Lists.newLinkedList();
        for (int j = 0; j < params.getLength(); j++) {
            Node p = params.item(j);
            if (p instanceof Element) {
                rst.add((Element) p);
            }
        }
        return rst;
    }

    public List<Element> getParamProcessorList(Element param) throws LinkException {
        try {
            NodeList params = (NodeList) xPath.evaluate("lk:processors/*", param, XPathConstants.NODESET);
            List<Element> rst = nodeList2ElementList(params);
            return rst;
        } catch (XPathExpressionException e) {
            throw new LinkException("parse input param failure", e);
        }
    }

    public Element getExecution() throws LinkException {
        try {
            return (Element) xPath.evaluate("/lk:directive/lk:execution", document, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new LinkException("parse execution failure", e);
        }
    }

    public List<Element> getExecutionElementList(Element execution) throws LinkException {
        try {
            NodeList params = (NodeList) xPath.evaluate("./*", execution, XPathConstants.NODESET);
            List<Element> rst = nodeList2ElementList(params);
            return rst;
        } catch (XPathExpressionException e) {
            throw new LinkException("parse execution failure", e);
        }
    }
}
