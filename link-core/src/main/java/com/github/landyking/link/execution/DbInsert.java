package com.github.landyking.link.execution;

import com.github.landyking.link.AbstractExecution;
import com.github.landyking.link.AbstractExecutionFactory;
import com.github.landyking.link.DataSourceManager;
import com.github.landyking.link.DirectiveMojo;
import com.github.landyking.link.exception.LinkException;
import org.w3c.dom.Element;

import javax.annotation.Resource;

/**
 * Created by landy on 2018/7/12.
 */
public class DbInsert implements AbstractExecutionFactory {
    @Resource
    private DataSourceManager dataSourceManager;

    public String tag() {
        return "dbInsert";
    }

    public AbstractExecution generate(Element element) {
        return new AbstractExecution(element) {

            @Override
            public void execute(DirectiveMojo mojo) throws LinkException {
                System.out.println("DbInsert.execute");
                String dataSourceId = element.getAttribute("dataSource");
                if (dataSourceManager.hasDataSource(dataSourceId)) {
                } else {
                    throw new LinkException("未配置数据源" + dataSourceId);
                }

            }
        };
    }
}
