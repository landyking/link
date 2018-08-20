package com.github.landyking.link;

import com.github.landyking.link.exception.DirectiveNotFoundException;
import com.github.landyking.link.exception.DirectiveParseException;
import com.github.landyking.link.exception.LinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by landy on 2018/7/5.
 * 指令管理器
 */
public class DirectiveManager implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DirectiveExec exec;
    private ApplicationContext applicationContext;

    public void setExec(DirectiveExec exec) {
        this.exec = exec;
    }

    public DirectiveMojo callDirective(String code, InputPot pot) throws LinkException {
        logger.info("调用指令: {}", code);
        DirectiveParser parser = loadDirectiveXml(code);
        LocalDictManager localDictManager = applicationContext.getBean(LocalDictManager.class);
        DirectiveMojo mojo = new DirectiveMojo(code, pot, parser, localDictManager);
        exec.execute(mojo);
        return mojo;
    }

    private DirectiveParser loadDirectiveXml(String code) throws LinkException {
        logger.debug("加载指令xml文件，编码为: {}", code);
        String path = code.replaceAll("\\.", "/");
        String location = "classpath:/link/dv/" + path + ".xml";
        Resource resource = applicationContext.getResource(location);
        if (resource.exists()) {
            if (resource.isReadable()) {
                try {
                    return new DirectiveParser(resource,this);
                } catch (Exception e) {
                    throw new DirectiveParseException(resource.toString() + "指令解析异常", e);
                }
            } else {
                throw new DirectiveNotFoundException("File " + location + " can't read.");
            }
        } else {
            throw new DirectiveNotFoundException("Can't found file " + location);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
