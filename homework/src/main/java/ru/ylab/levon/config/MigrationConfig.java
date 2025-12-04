package ru.ylab.levon.config;

import java.util.Properties;

import liquibase.command.CommandScope;

/**
 * Конфигурация Liquibase-миграций.
 */
public class MigrationConfig {
    /**
     * Применяет Liquibase-миграции к базе данных.
     *
     * @param props конфигурация приложения
     */
    public static void runMigrations(Properties props) {
        try {
            new CommandScope("update")
                    .addArgumentValue("changeLogFile", props.getProperty(ConfigKeys.LIQUIBASE_CHANGELOG))
                    .addArgumentValue("url", props.getProperty(ConfigKeys.DB_URL))
                    .addArgumentValue("username", props.getProperty(ConfigKeys.DB_USERNAME))
                    .addArgumentValue("password", props.getProperty(ConfigKeys.DB_PASSWORD))
                    .addArgumentValue("defaultSchemaName", props.getProperty(ConfigKeys.LIQUIBASE_DEFAULT_SCHEMA))
                    .addArgumentValue("liquibaseSchemaName", props.getProperty(ConfigKeys.LIQUIBASE_SERVICE_SCHEMA))
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration error", e);
        }
    }
}
