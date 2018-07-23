package com.github.landyking.link.execution.custom;

import com.github.landyking.link.AbstractExecution;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ExecuteResult;
import com.github.landyking.link.exception.LinkException;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by landy on 2018/7/23.
 */
public class InputGotoOutput extends AbstractExecution {
    public InputGotoOutput(Element element) {
        super(element);
    }

    @Override
    public void execute(DirectiveMojo mojo) throws LinkException {
        Map<String, Object> input = mojo.getProcessedInputParamMap();
        String id = element.getAttribute("id");
        ExecuteResult rst = new ExecuteResult();
        rst.setData(Arrays.asList(input));
        mojo.setExecuteResult(id, rst);
    }
}
