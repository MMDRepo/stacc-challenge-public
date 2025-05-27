package no.stacc.payforjoy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {

   @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        log.info("Configuring H2 in-memory data source for PayForJoy");
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .username("sa")
                .password("password")
                .driverClassName("org.h2.Driver")
                .build();
    }



    @Bean
    @ConditionalOnProperty(
            value = "payforjoy.database.enable-query-logging",
            havingValue = "true",
            matchIfMissing = false
    )
    public DatabaseQueryLogger queryLogger() {
        log.info("Enabling database query logging");
        return new DatabaseQueryLogger();
    }

    // Custom query logger class
    static class DatabaseQueryLogger {
        private static final org.slf4j.Logger queryLog =
                org.slf4j.LoggerFactory.getLogger("payforjoy.database.queries");

        public void logQuery(String query, Object... params) {
            queryLog.debug("SQL Query: {} | Parameters: {}", query, params);
        }
    }
}