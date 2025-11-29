package ru.ylab.levon.config;

import java.util.Properties;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Конфигурация подключения к базе данных.
 * <p>
 * Содержит фабричный метод для создания и настройки пула соединений HikariCP
 * на основе параметров, указанных в файле конфигурации.
 *
 * <p>Ожидаемые свойства:
 * <ul>
 *   <li>{@code db.url} — JDBC-URL подключения</li>
 *   <li>{@code db.username} — имя пользователя базы данных</li>
 *   <li>{@code db.password} — пароль пользователя</li>
 * </ul>
 *
 * @see javax.sql.DataSource
 * @see com.zaxxer.hikari.HikariDataSource
 */
public class DatabaseConfig {
    /**
     * Создаёт и настраивает {@link DataSource} на основе параметров конфигурации.
     *
     * @param props объект {@link Properties}, содержащий параметры подключения
     * @return настроенный пул соединений {@link DataSource}
     * @throws RuntimeException если отсутствуют необходимые параметры конфигурации
     */
    public static DataSource createDataSource(Properties props) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(props.getProperty(ConfigKeys.DB_URL));
        config.setUsername(props.getProperty(ConfigKeys.DB_USERNAME));
        config.setPassword(props.getProperty(ConfigKeys.DB_PASSWORD));

        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }
}
