package org.habitApp.database;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class LiquibaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private String jdbcUrl;
    private String username;
    private String password;
    private String changeLogFile;

    public LiquibaseInitializer() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            this.jdbcUrl = properties.getProperty("jdbc.url");
            this.username = properties.getProperty("jdbc.username");
            this.password = properties.getProperty("jdbc.password");
            this.changeLogFile = properties.getProperty("liquibase.changeLogFile");

            // Ручная регистрация драйвера PostgreSQL
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver Registered!");
        } catch (Exception e) {
            System.err.println("Ошибка загрузки настроек: " + e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
            System.out.println("Liquibase миграции успешно выполнены.");
        } catch (SQLException | LiquibaseException e) {
            System.err.println("Ошибка выполнения миграций Liquibase: " + e.getMessage());
//            e.printStackTrace();
        }
    }
}
