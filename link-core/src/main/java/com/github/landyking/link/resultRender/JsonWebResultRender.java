package com.github.landyking.link.resultRender;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ExecuteResult;
import com.github.landyking.link.ExecutionEndingData;
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
        ExecutionEndingData extra = mojo.getEndingData();
        ObjectNode rst = JsonUtils.JSON.createObjectNode();
        rst.put("success", true);
        rst.put("message", "ok");
        ObjectNode data = rst.putObject("data");
        if (extra instanceof ExecuteResult) {
            ExecuteResult er = (ExecuteResult) extra;
            data.put("effectCount", er.getEffectCount());
            data.put("totalCount", er.getTotalCount());
            data.put("primaryKey", Texts.toStr(er.getPrimaryKeyValue()));
        }
        Object afterOutput = mojo.getAfterOutput();
        if (afterOutput == null) {
            //empty
        } else if (afterOutput instanceof Iterable) {
            ArrayNode list = data.putArray("list");
            for (Object o : ((Iterable) afterOutput)) {
                list.addPOJO(o);
            }
        } else {
            data.putPOJO("one", afterOutput);
        }
        output(response, rst);
    }

    protected void output(HttpServletResponse response, ObjectNode rst) throws IOException {
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
