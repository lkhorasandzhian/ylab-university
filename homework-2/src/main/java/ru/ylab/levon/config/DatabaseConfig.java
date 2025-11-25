package ru.ylab.levon.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Конфигурация подключения к базе данных.
 * <p>
 * Содержит фабричный метод для создания пула соединений HikariCP.
 */
public class DatabaseConfig {
    /**
     * Создаёт и настраивает {@link DataSource} на основе HikariCP.
     *
     * @param url      JDBC-URL подключения
     * @param username имя пользователя базы данных
     * @param password пароль пользователя базы данных
     * @return настроенный источник соединений
     */
    public static DataSource createDataSource(
            String url,
            String username,
            String password
    ) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }
}
