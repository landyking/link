package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.spel.SpelPair;
import com.github.landyking.link.spel.SpelUtils;
import com.github.landyking.link.util.LkTools;
import com.github.landyking.link.util.Texts;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedCaseInsensitiveMap;
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
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by landy on 2018/7/5.
 * 指令解析器
 */
public class DirectiveParser {
    public static final String LINK_1_0_XSD = "http://www.github.com/landyking/link/link-1.0.xsd";
    public static final String XSD_LOCATION = "/link/link-1.0.xsd";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final XPath xPath = initXPath();
    private transient DirectiveMojo directiveMojo;
    private final DirectiveManager directiveManager;

    public void setDirectiveMojo(DirectiveMojo directiveMojo) {
        this.directiveMojo = directiveMojo;
    }

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
    private final List<Element> outputParamList;

    public DirectiveParser(Resource resource, DirectiveManager directiveManager) throws Exception {
        this.directiveManager = directiveManager;
        logger.debug("开始解析文件: {}", resource.getURL().toString());
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
        DocumentBuilderFactory factory = createDocumentBuilderFactory();
        DocumentBuilder documentBuilder = createDocumentBuilder(factory, entityResolver, errorHandler);
        document = documentBuilder.parse(resource.getURL().toString());
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(getClass().getResource(XSD_LOCATION));
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(document));
        outputParamList = initOutputParam();
    }

    private List<Element> initOutputParam() throws LinkException {
        List<Element> rst = Lists.newLinkedList();
        Element ele = getSubElement(document, "/lk:directive/lk:output");
        Deque<Element> stack = Lists.newLinkedList();
        stack.push(ele);
        while (!stack.isEmpty()) {
            Element poll = stack.poll();
            rst.add(poll);
            List<Element> tmpList = reverseOutputChildList(poll);
            for (Element e : tmpList) {
                stack.push(e);
            }
        }
        return rst;
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

    public String getDirectiveCode() {
        return directiveCode;
    }

    public void setDirectiveCode(String directiveCode) {
        this.directiveCode = directiveCode;
    }

    public List<Element> getInputParamList() throws LinkException {
        return getSubElementList(document, "/lk:directive/lk:input/lk:param");
    }

    public List<Element> getOutputParamList() throws LinkException {
        return getSubElementList(document, "/lk:directive/lk:output/lk:param");
    }

    public List<Element> nodeList2ElementList(NodeList params) {
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
        return getSubElementList(param, "lk:processors/*");
    }

    public Element getExecution() throws LinkException {
        return getSubElement(document, "/lk:directive/lk:execution");
    }

    public String getParamText(Element config, String pname) throws LinkException {
        return Texts.toStr(getParam(config, pname));
    }

    public Object getParam(Element config, String pname) throws LinkException {
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
                        Boolean useExp = LkTools.isTrue(tmp.getAttribute("useExp"));
                        if (useExp) {
                            SpelPair sp = SpelUtils.getSpelPair(directiveMojo);
                            return sp.getExp().parseExpression(tmp.getTextContent()).getValue(sp.getCtx());
                        } else {
                            return tmp.getTextContent();
                        }
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

    public List<AbstractExecution> getExecutionList(Element execution) throws LinkException {
        List<AbstractExecution> rst = Lists.newLinkedList();
        List<Element> list = getSubElementList(execution, "./*");
        Map<String, AbstractExecutionFactory> tmpList = this.directiveManager.getApplicationContext().getBeansOfType(AbstractExecutionFactory.class);
        LinkedCaseInsensitiveMap<AbstractExecutionFactory> map = new LinkedCaseInsensitiveMap<AbstractExecutionFactory>();
        for (AbstractExecutionFactory a : tmpList.values()) {
            map.put(a.tag(), a);
        }
        for (Element one : list) {
            AbstractExecutionFactory factory = map.get(one.getNodeName());
            if (factory == null) {
                throw new LinkException("无法为节点" + one.getNodeName() + "找到指令执行器");
            } else {
                rst.add(factory.generate(one));
            }
        }
        return rst;
    }

    public Element getExecutionEnding() throws LinkException {
        return getSubElement(document, "/lk:directive/lk:executionEnding");
    }

    public Element getResultRender() throws LinkException {
        return getSubElement(document, "/lk:directive/lk:resultRender");
    }

    public Iterable<Element> getOutputIterator() throws LinkException {
        return outputParamList;
    }

    public Element getOutputNode() throws LinkException {
        return getSubElement(document, "/lk:directive/lk:output");
    }


    public List<Element> reverseOutputChildList(Element ele) {
        List<Element> rst = Lists.newLinkedList();
        NodeList nodes = ele.getChildNodes();
        for (int i = nodes.getLength(); i > 0; i--) {
            Node item = nodes.item(i - 1);
            String nodeName = item.getNodeName();
            if (nodeName.equals("list") || nodeName.equals("map") || nodeName.equals("param")) {
                rst.add((Element) item);
            }
        }
        return rst;
    }

    public String getFullPath(Node e, boolean withName) {
        StringBuilder sb = new StringBuilder();
        if (withName) {
            sb.append("[name=" + ((Element) e).getAttribute("name") + "]");
        }
        while (e != null && e != document) {
            sb.insert(0, "/" + e.getNodeName());
            e = e.getParentNode();
        }
        return sb.toString();
    }
}
