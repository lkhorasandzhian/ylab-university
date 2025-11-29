package ru.ylab.levon.config;

public final class ConfigKeys {
    // Static class.
    private ConfigKeys() {}

    // Database.
    public static final String DB_URL = "db.url";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";

    // Liquibase.
    public static final String LIQUIBASE_CHANGELOG = "liquibase.changelog";
    public static final String LIQUIBASE_DEFAULT_SCHEMA = "liquibase.defaultSchemaName";
    public static final String LIQUIBASE_SERVICE_SCHEMA = "liquibase.liquibaseSchemaName";
}
