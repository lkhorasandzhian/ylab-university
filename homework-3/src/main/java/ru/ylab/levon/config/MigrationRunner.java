package ru.ylab.levon.config;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.command.CommandScope;

import java.util.Properties;

public class MigrationRunner {

    /**
     * Применяет Liquibase-миграции к базе данных.
     */
    public void run(HikariDataSource ds, Properties props) {
        try {
            new CommandScope("update")
                    .addArgumentValue("changeLogFile", props.getProperty("liquibase.changelog"))
                    .addArgumentValue("url", ds.getJdbcUrl())
                    .addArgumentValue("username", ds.getUsername())
                    .addArgumentValue("password", ds.getPassword())
                    .execute();

            System.out.println("Liquibase migrations applied");
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration error", e);
        }
    }
}
