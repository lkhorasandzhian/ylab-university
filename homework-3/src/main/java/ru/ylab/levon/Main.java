package ru.ylab.levon;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.ylab.levon.service.*;

/**
 * Главный класс приложения Product Catalog Service.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Загрузку данных из хранилища;</li>
 *     <li>Создание репозиториев и сервисов;</li>
 *     <li>Инициализацию и запуск консольного интерфейса;</li>
 *     <li>Регистрацию механизма автосохранения при завершении работы приложения.</li>
 * </ul>
 * <p>
 * Если список пользователей пуст, создаётся администратор по умолчанию.
 */
public class Main {
    /**
     * Точка входа в приложение.
     * <p>
     * Выполняет инициализацию всех компонентов системы,
     * подготавливает хранилище данных и запускает консольное меню.
     *
     * @param args аргументы командной строки (не используются)
     */
    @SuppressWarnings("UnnecessaryModifier")
    public static void main(@SuppressWarnings("unused") String[] args) {
        Properties props = loadProperties("application.properties");
        HikariDataSource dataSource = initDataSource(props);
        runMigrations(dataSource, props);

        System.out.println("БД инициализирована. Приложение запущено в контейнере сервлетов.");
    }

    private static Properties loadProperties(@SuppressWarnings("SameParameterValue") String file) {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new RuntimeException("Не найден файл конфигурации: " + file);
            }
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }

    private static HikariDataSource initDataSource(Properties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(600000);

        return new HikariDataSource(config);
    }

    private static void runMigrations(HikariDataSource ds, Properties props) {
        String changelog = props.getProperty("liquibase.changelog");

        try {
            new liquibase.command.CommandScope("update")
                    .addArgumentValue("changeLogFile", changelog)
                    .addArgumentValue("url", ds.getJdbcUrl())
                    .addArgumentValue("username", ds.getUsername())
                    .addArgumentValue("password", ds.getPassword())
                    .execute();

            System.out.println("Liquibase-миграции успешно применены.");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка миграции Liquibase", e);
        }
    }
}
