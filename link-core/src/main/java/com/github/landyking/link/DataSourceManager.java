package com.github.landyking.link;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by landy on 2018/7/12.
 */
public class DataSourceManager implements ApplicationContextAware,InitializingBean {
    private ApplicationContext application;
    private Map<String, DataSource> dataSourceMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application=applicationContext;
    }

    public boolean hasDataSource(String dataSourceId) {
        return dataSourceMap.containsKey(dataSourceId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceMap = application.getBeansOfType(DataSource.class);
    }
}
