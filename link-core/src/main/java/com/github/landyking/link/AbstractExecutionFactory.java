package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
import org.w3c.dom.Element;

/**
 * Created by landy on 2018/7/12.
 */
public interface AbstractExecutionFactory {
    public String tag();

    public AbstractExecution generate(Element element)throws LinkException;
}
