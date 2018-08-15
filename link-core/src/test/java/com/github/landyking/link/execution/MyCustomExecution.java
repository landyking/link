package com.github.landyking.link.execution;

import com.github.landyking.link.AbstractExecution;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ExecuteResult;
import com.github.landyking.link.exception.LinkException;
import org.w3c.dom.Element;

import java.util.Collections;

/**
 * Created by landy on 2018/8/15.
 */
public class MyCustomExecution extends AbstractExecution {
    public MyCustomExecution(Element element) {
        super(element);
    }

    @Override
    public void execute(DirectiveMojo mojo) throws LinkException {
        String id = executionId();
        Object param = mojo.getParser().getParam(element, "param");
        ExecuteResult rst = new ExecuteResult();
        rst.setData(Collections.singletonList(Collections.singletonMap("param", param)));
        mojo.setExecuteResult(id, rst);
    }
}
