package com.github.landyking.link.servlet;

import com.github.landyking.link.DirectiveManager;
import com.github.landyking.link.exception.DirectiveNotFoundException;
import com.github.landyking.link.exception.LinkException;
import com.github.landyking.link.pot.WebInputPot;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: landy
 * @date: 2018-05-17 11:05
 */
public class DirectiveServlet extends AbstractServlet {
    @Override
    protected void doWork(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String directive = req.getParameter("directive");
        if (StringUtils.hasText(directive)) {
            DirectiveManager directiveManager = getContext().getBean(DirectiveManager.class);
            try {
                directiveManager.callDirective(directive, new WebInputPot(req, resp,getServletContext()));
            } catch (DirectiveNotFoundException e) {
                String msg = "Can't found directive: " + directive;
                LOG.info(msg);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
            } catch (LinkException e) {
                LOG.error("未知服务器异常", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "内部异常:" + e.toString());
            }
        } else {
            String msg = "Can't found parameter: directive";
            LOG.info(msg);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }
}
