package org.habitApp.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DatabaseConfig {
    @Bean
    public Properties yamlPropertySource() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        return yaml.getObject();
    }

    @Bean
    public DataSource dataSource(Properties yamlPropertySource) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(yamlPropertySource.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(yamlPropertySource.getProperty("jdbc.url"));
        dataSource.setUsername(yamlPropertySource.getProperty("jdbc.username"));
        dataSource.setPassword(yamlPropertySource.getProperty("jdbc.password"));
        return dataSource;
    }

    @Bean
    public LiquibaseInitializer liquibaseInitializer(Properties yamlPropertySource) {
        return new LiquibaseInitializer(yamlPropertySource);
    }
}