package ru.ylab.levon.config;

import java.util.Properties;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Фабрика для создания и настройки источника соединений с базой данных.
 * <p>
 * Использует пул соединений HikariCP и параметры конфигурации,
 * переданные через {@link Properties}. Класс инкапсулирует всю логику
 * конфигурирования подключения к базе данных.
 */
public class DataSourceFactory {
    /**
     * Создаёт и настраивает {@link DataSource} на основе HikariCP,
     * используя параметры, указанные в файле конфигурации.
     * <p>
     * Ожидаемые свойства:
     * <ul>
     *     <li><b>db.url</b> — JDBC-URL подключения;</li>
     *     <li><b>db.username</b> — имя пользователя;</li>
     *     <li><b>db.password</b> — пароль пользователя.</li>
     * </ul>
     *
     * @param props объект {@link Properties}, содержащий параметры подключения
     * @return сконфигурированный пул соединений HikariCP
     */
    public HikariDataSource create(Properties props) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setDriverClassName("org.postgresql.Driver");

        // Настройки пула соединений.
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }
}
