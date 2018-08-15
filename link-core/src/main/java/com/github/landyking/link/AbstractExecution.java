package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/10.
 */
public abstract class AbstractExecution {
    protected final Element element;

    public String executionId() {
        return element.getAttribute("id");
    }

    public AbstractExecution(Element element) {
        this.element = element;
    }

    public String tag() {
        return getClass().getSimpleName();
    }

    public Element getElement() {
        return element;
    }

    public abstract void execute(DirectiveMojo mojo) throws LinkException;
}
