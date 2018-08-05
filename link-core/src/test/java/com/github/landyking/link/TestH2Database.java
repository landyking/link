package com.github.landyking.link;

import org.flywaydb.core.Flyway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author: landy
 * @date: 2018-08-05 22:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/link/linkCore.xml")
public class TestH2Database {
    @Resource
    private DataSourceManager dataSourceManager;

    @Test
    public void test11() throws Exception {
        // Create the Flyway instance
        Flyway flyway = new Flyway();

        // Point it to the database
        flyway.setDataSource(dataSourceManager.getDataSource("test"));
        // Start the migration
        flyway.migrate();

    }
}
