package com.github.landyking.link.execution.custom;

import com.github.landyking.link.AbstractExecution;
import com.github.landyking.link.DirectiveExec;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.exception.LinkException;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;

import javax.annotation.Resource;

/**
 * Created by landy on 2018/7/12.
 */
public class HelloCustom extends AbstractExecution {

    public HelloCustom(Element element) {
        super(element);
    }

    @Override
    public void execute(DirectiveMojo mojo) throws LinkException {
        System.out.println("HelloCustom.execute");
        System.out.println(mojo.getParser().getParam(element, "name"));
    }
}
