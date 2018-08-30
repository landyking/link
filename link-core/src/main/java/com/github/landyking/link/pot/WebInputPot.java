package com.github.landyking.link.pot;

import com.github.landyking.link.InputPot;
import com.github.landyking.link.util.Texts;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by landy on 2018/8/1.
 */
public class WebInputPot implements InputPot {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;

    public WebInputPot(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    @Override
    public String getInputParamText(String name) {
        String attribute = Texts.toStr(request.getAttribute(name));
        if (Texts.hasText(attribute)) {
            return attribute;
        }
        return request.getParameter(name);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
