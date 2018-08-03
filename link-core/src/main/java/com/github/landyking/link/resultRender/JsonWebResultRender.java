package com.github.landyking.link.resultRender;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.util.JsonUtils;
import com.github.landyking.link.util.Texts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by landy on 2018/8/1.
 */
public class JsonWebResultRender extends WebResultRender {
    @Override
    protected void renderWebSuccess(HttpServletRequest request, HttpServletResponse response, DirectiveMojo mojo) throws Exception {
        ObjectNode rst = JsonUtils.JSON.createObjectNode();
        rst.put("success", true);
        rst.put("message", "ok");
        Object afterOutput = mojo.getAfterOutput();
        ObjectNode data = rst.putPOJO("data", afterOutput);

        output(response, rst);
    }


    protected void output(HttpServletResponse response, Object rst) throws IOException {
        response.setContentType(JsonUtils.DEFAULT_CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JsonUtils.toStr(rst));
    }

    @Override
    protected void renderWebException(HttpServletRequest request, HttpServletResponse response, DirectiveMojo mojo, Exception exception) throws Exception {
        ObjectNode rst = JsonUtils.JSON.createObjectNode();
        rst.put("success", false);
        rst.put("message", Texts.toStr(exception));
        rst.put("exception", exception.getClass().getName());
        output(response, rst);
    }
}
