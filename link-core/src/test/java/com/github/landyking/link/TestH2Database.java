package com.github.landyking.link;

import org.flywaydb.core.Flyway;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author: landy
 * @date: 2018-08-05 22:37
 */
public class TestH2Database {
    private static DataSourceManager dataSourceManager;
    private static ClassPathXmlApplicationContext app;
    private static DirectiveManager dm;

    @BeforeClass
    public static void initH2Database() throws Exception {
        app = new ClassPathXmlApplicationContext("classpath*:/link/linkCore.xml");
        dataSourceManager = app.getBean(DataSourceManager.class);
        // Create the Flyway instance
        Flyway flyway = new Flyway();

        // Point it to the database
        flyway.setDataSource(dataSourceManager.getDataSource("test"));
        // Start the migration
        flyway.migrate();
        dm = app.getBean(DirectiveManager.class);
    }

    public static DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public static ClassPathXmlApplicationContext getApp() {
        return app;
    }

    public static DirectiveManager getDm() {
        return dm;
    }
}
