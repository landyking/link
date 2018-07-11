package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/10.
 */
public abstract class AbstractExecution {
    public String tag() {
        return getClass().getSimpleName();
    }

    public abstract void execute(Element one, DirectiveMojo mojo) throws LinkException;
}
