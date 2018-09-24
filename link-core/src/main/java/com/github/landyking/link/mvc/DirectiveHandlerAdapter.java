package com.github.landyking.link.mvc;

import com.github.landyking.link.DirectiveExec;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.DirectiveParser;
import com.github.landyking.link.pot.WebInputPot;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by landy on 2018/9/24.
 */
public class DirectiveHandlerAdapter implements HandlerAdapter, ApplicationContextAware {

    private ApplicationContext application;
    DirectiveExec directiveExec;

    public void setDirectiveExec(DirectiveExec directiveExec) {
        this.directiveExec = directiveExec;
    }

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof DirectiveParser);
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        DirectiveParser parser = (DirectiveParser) handler;
        WebInputPot port = new WebInputPot(request, response, request.getServletContext());
        DirectiveMojo mojo = new DirectiveMojo(parser.getDirectiveCode(), port, parser, application);
        directiveExec.execute(mojo);
        return null;
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1L;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application = applicationContext;
    }
}