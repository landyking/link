package com.github.landyking.link.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractServlet extends HttpServlet {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    private WebApplicationContext context;
    private MultipartResolver multipartResolver;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = this.getServletContext();
        context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        if (context.containsBean("multipartResolver")) {
            multipartResolver = context.getBean("multipartResolver", MultipartResolver.class);
        } else {
            multipartResolver = context.getBean("defaultMultipartResolver", MultipartResolver.class);
            LOG.info("Can't found [multipartResolver], will use default MultipartResolver: {}",multipartResolver.toString());
        }
    }

    public WebApplicationContext getContext() {
        return context;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doService(req, resp);
    }

    private void doService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletRequest processedReq = null;
        boolean multipart = false;
        try {
            processedReq = checkMultipart(req);
            multipart = processedReq != req;
            doWork(processedReq, resp);
        } finally {
            if (multipart) {
                cleanupMultipart(processedReq);
            }
        }
    }

    /**
     * Convert the request into a multipart request, and make multipart resolver available.
     * <p>If no multipart resolver is set, simply use the existing request.
     *
     * @param request current HTTP request
     * @return the processed request (multipart wrapper if necessary)
     * @see MultipartResolver#resolveMultipart
     */
    protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
            if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
                LOG.debug("Request is already a MultipartHttpServletRequest - if not in a forward, " +
                        "this typically results from an additional MultipartFilter in web.xml");
            } else if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) instanceof MultipartException) {
                LOG.debug("Multipart resolution failed for current request before - " +
                        "skipping re-resolution for undisturbed error rendering");
            } else {
                return this.multipartResolver.resolveMultipart(request);
            }
        }
        // If not returned before: return original request.
        return request;
    }

    /**
     * Clean up any resources used by the given multipart request (if any).
     *
     * @param request current HTTP request
     * @see MultipartResolver#cleanupMultipart
     */
    protected void cleanupMultipart(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (multipartRequest != null) {
            this.multipartResolver.cleanupMultipart(multipartRequest);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doService(req, resp);
    }

    protected abstract void doWork(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
}