package com.github.landyking.link;

import com.github.landyking.link.util.DBType;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by landy on 2018/7/12.
 */
public class DataSourceManager implements ApplicationContextAware, InitializingBean {
    private ApplicationContext application;
    private Map<String, DataSource> dataSourceMap;
    private Map<String, DataSourceTransactionManager> dataSourceTransactionManagerMap = Maps.newHashMap();
    private Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap = Maps.newHashMap();
    private Map<String, DBType> dbTypeMap = Maps.newHashMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.application = applicationContext;
    }

    public boolean hasDataSource(String dataSourceId) {
        return dataSourceMap.containsKey(dataSourceId);
    }

    public DataSource getDataSource(String dataSourceId) {
        return dataSourceMap.get(dataSourceId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceMap = application.getBeansOfType(DataSource.class);
        for (String key : dataSourceMap.keySet()) {
            DataSource ds = dataSourceMap.get(key);
            DBType type = (DBType) JdbcUtils.extractDatabaseMetaData(ds, new DatabaseMetaDataCallback() {
                @Override
                public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                    String databaseProductName = dbmd.getDatabaseProductName();
                    if (databaseProductName.toUpperCase().contains("ORACLE")) {
                        return DBType.Oracle;
                    } else if (databaseProductName.toUpperCase().contains("MYSQL")) {
                        return DBType.Mysql;
                    } else if (databaseProductName.toUpperCase().contains("HIVE")) {
                        return DBType.ApacheHive;
                    } else if (databaseProductName.toUpperCase().contains("H2")) {
                        return DBType.H2Database;
                    } else {
                        throw new RuntimeException("不支持的数据库类型:" + databaseProductName);
                    }
                }
            });
            dbTypeMap.put(key, type);
        }
    }

    /**
     * 获取事务管理器。如果事务管理器不存在，获取对应的数据源进行创建。
     *
     * @param dataSourceId
     * @return
     */
    private synchronized DataSourceTransactionManager getTransactionManager(String dataSourceId) {
        DataSourceTransactionManager transactionManager = dataSourceTransactionManagerMap.get(dataSourceId);
        if (transactionManager == null) {
            DataSource ds = dataSourceMap.get(dataSourceId);
            transactionManager = new DataSourceTransactionManager(ds);
            dataSourceTransactionManagerMap.put(dataSourceId, transactionManager);
        }
        return transactionManager;
    }

    public TransactionTemplate getTransactionTemplate(String dataSourceId, TransactionDefinition transactionDefinition) {
        DataSourceTransactionManager transactionManager = getTransactionManager(dataSourceId);
        if (transactionDefinition == null) {
            transactionDefinition = new DefaultTransactionDefinition();
        }
        return new TransactionTemplate(transactionManager, transactionDefinition);
    }

    /**
     * 获取jdbc执行模板，如果不存在则创建
     *
     * @param dataSourceId
     * @return
     */
    public synchronized NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String dataSourceId) {
        NamedParameterJdbcTemplate jdbcTemplate = namedParameterJdbcTemplateMap.get(dataSourceId);
        if (jdbcTemplate == null) {
            DataSource dataSource = dataSourceMap.get(dataSourceId);
            Assert.notNull(dataSource, "数据源" + dataSourceId + "为空");
            jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            namedParameterJdbcTemplateMap.put(dataSourceId, jdbcTemplate);
        }
        return jdbcTemplate;
    }

    public DBType getDataBaseType(String dataSourceId) {
        return dbTypeMap.get(dataSourceId);
    }
}
