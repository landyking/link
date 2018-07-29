package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by landy on 2018/7/5.
 * 指令解析器
 */
public class LocalDictParser {
    public static final String DICT_1_0_XSD = "http://www.github.com/landyking/dict/dict-1.0.xsd";
    public static final String XSD_LOCATION = "/link/dict-1.0.xsd";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final XPath xPath = initXPath();

    private static XPath initXPath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                if (prefix == null) throw new NullPointerException("Null prefix");
                else if ("dict".equals(prefix)) return "http://www.github.com/landyking/dict";
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

    private final Document document;

    public LocalDictParser(Resource resource) throws Exception {
        logger.debug("开始解析文件: {}", resource.getURL().toString());
//        DefaultDocumentLoader documentLoader = new DefaultDocumentLoader();
        DefaultHandler errorHandler = new DefaultHandler();
        EntityResolver entityResolver = new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (systemId.equals(DICT_1_0_XSD)) {
                    return new InputSource(getClass().getResourceAsStream(XSD_LOCATION));
                } else {
                    return null;
                }
            }
        };
        DocumentBuilderFactory factory = createDocumentBuilderFactory();
        DocumentBuilder documentBuilder = createDocumentBuilder(factory, entityResolver, errorHandler);
        document = documentBuilder.parse(resource.getURL().toString());
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(getClass().getResource(XSD_LOCATION));
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(document));
    }

    /**
     * JAXP attribute used to configure the schema language for validation.
     */
    private static final String SCHEMA_LANGUAGE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    /**
     * JAXP attribute value indicating the XSD schema language.
     */
    private static final String XSD_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";

    protected DocumentBuilderFactory createDocumentBuilderFactory()
            throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        try {
            factory.setAttribute(SCHEMA_LANGUAGE_ATTRIBUTE, XSD_SCHEMA_LANGUAGE);
        } catch (IllegalArgumentException ex) {
            ParserConfigurationException pcex = new ParserConfigurationException(
                    "Unable to validate using XSD: Your JAXP provider [" + factory +
                            "] does not support XML Schema. Are you running on Java 1.4 with Apache Crimson? " +
                            "Upgrade to Apache Xerces (or Java 1.5) for full XSD support.");
            pcex.initCause(ex);
            throw pcex;
        }
        return factory;
    }

    protected DocumentBuilder createDocumentBuilder(
            DocumentBuilderFactory factory, EntityResolver entityResolver, ErrorHandler errorHandler)
            throws ParserConfigurationException {

        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        if (entityResolver != null) {
            docBuilder.setEntityResolver(entityResolver);
        }
        if (errorHandler != null) {
            docBuilder.setErrorHandler(errorHandler);
        }
        return docBuilder;
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


    public String getParam(Element config, String pname) {
        if (config.hasAttribute(pname)) {
            //首先从尝试从属性中解析该参数
            return config.getAttribute(pname);
        } else {
            //接着尝试从同名子节点解析该参数
            NodeList childNodes = config.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item.getNodeName().equals(pname)) {
                    return item.getTextContent();
                }
            }
            //最后尝试从特定子节点解析该参数
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item.getNodeName().equals("prop") && item instanceof Element) {
                    Element tmp = (Element) item;
                    String name = tmp.getAttribute("name");
                    if (name.equals(pname)) {
                        return tmp.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    public List<Element> getSubElementList(Object ele, String xpath) throws LinkException {
        try {
            NodeList params = (NodeList) xPath.evaluate(xpath, ele, XPathConstants.NODESET);
            List<Element> rst = nodeList2ElementList(params);
            return rst;
        } catch (XPathExpressionException e) {
            throw new LinkException("parse xpath [" + xpath + "] for element [" + ele.toString() + "] error", e);
        }
    }

    public Element getSubElement(Object ele, String xpath) throws LinkException {
        List<Element> list = getSubElementList(ele, xpath);
        return Iterables.getFirst(list, null);
    }


    public LinkedList<LocalDict> parseDictList() throws LinkException {
        LinkedList<LocalDict> rst = Lists.newLinkedList();
        List<Element> dictList = getSubElementList(document, "dict:dicts/dict:dict");
        for (Element dict : dictList) {
            String name = dict.getAttribute("name");
            String desc = getParam(dict, "desc");
            LocalDict localDict = new LocalDict(name, desc);
            rst.add(localDict);

            List<Element> itemList = getSubElementList(dict, "dict:item");
            for (Element item : itemList) {
                String code = item.getAttribute("code");
                String marker = item.getAttribute("marker");
                String content = item.getTextContent();
                LocalDictItem localDictItem = new LocalDictItem(code, content, marker);
                localDict.getItems().put(code, localDictItem);
            }
        }
        return rst;
    }
}
