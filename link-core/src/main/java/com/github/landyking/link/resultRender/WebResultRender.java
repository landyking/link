package com.github.landyking.link.resultRender;

import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.ResultRender;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.pot.WebInputPot;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by landy on 2018/8/1.
 */
public abstract class WebResultRender implements ResultRender {
    @Override
    public void render(DirectiveMojo mojo) throws Exception {
        if (mojo.getPot() instanceof WebInputPot) {
            WebInputPot wip = (WebInputPot) mojo.getPot();
            try {
                renderWeb(wip.getRequest(), wip.getResponse(), mojo);
            } catch (Exception e) {
                throw new LinkException("渲染执行结果异常", e);
            }
        } else {
            throw new LinkException("执行结果无法被渲染");
        }
    }

    protected void renderWeb(HttpServletRequest request, HttpServletResponse response, DirectiveMojo mojo) throws Exception {
        if (mojo.getException() != null) {
            renderWebException(request, response, mojo, mojo.getException());
        } else {
            renderWebSuccess(request, response, mojo);
        }
    }

    protected abstract void renderWebSuccess(HttpServletRequest request, HttpServletResponse response, DirectiveMojo mojo) throws Exception;

    protected abstract void renderWebException(HttpServletRequest request, HttpServletResponse response, DirectiveMojo mojo, Exception exception) throws Exception;
}
