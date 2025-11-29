package ru.ylab.levon;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.ylab.levon.config.ConfigKeys;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.repository.jdbc.JdbcAuditRepository;
import ru.ylab.levon.repository.jdbc.JdbcProductRepository;
import ru.ylab.levon.repository.jdbc.JdbcUserRepository;
import ru.ylab.levon.service.*;
import ru.ylab.levon.view.ConsoleMenu;

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
        System.out.println("\n=== Product Catalog Service ===\n");

        Properties props = loadProperties("application.properties");

        HikariDataSource dataSource = initDataSource(props);

        runMigrations(dataSource, props);

        var productRepo = new JdbcProductRepository(dataSource);
        var userRepo = new JdbcUserRepository(dataSource);
        var auditRepo = new JdbcAuditRepository(dataSource);

        var cacheService = new CacheService<String, List<Product>>(20);
        var catalogService = new CatalogService(productRepo, cacheService);
        var userService = new UserService(userRepo);
        var auditService = new AuditService(auditRepo);

        // Создание администратора по умолчанию, если база пользователей пуста.
        if (userRepo.findByUsername("admin") == null) {
            userService.register(new UserCreateDto("admin", "admin", Role.ADMIN));
        }

        // Запуск консольного интерфейса приложения.
        var menu = new ConsoleMenu(catalogService, userService, auditService);
        menu.run();
    }

    /**
     * Загружает properties-файл из classpath.
     *
     * @param file имя файла конфигурации
     * @return объект {@link Properties}, содержащий параметры конфигурации
     * @throws RuntimeException если файл не найден или возникла ошибка чтения
     */
    private static Properties loadProperties(@SuppressWarnings("SameParameterValue") String file) {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(file)) {
            if (inputStream == null) {
                throw new RuntimeException("Не найден файл конфигурации: " + file);
            }
            Properties props = new Properties();
            props.load(inputStream);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }

    /**
     * Инициализирует пул соединений HikariCP,
     * применяя параметры из файла конфигурации.
     *
     * @param props объект с конфигурационными параметрами
     * @return настроенный {@link HikariDataSource}
     */
    private static HikariDataSource initDataSource(Properties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty(ConfigKeys.DB_URL));
        config.setUsername(props.getProperty(ConfigKeys.DB_USERNAME));
        config.setPassword(props.getProperty(ConfigKeys.DB_PASSWORD));

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(600000);

        return new HikariDataSource(config);
    }

    /**
     * Запускает применение Liquibase-миграций на основе параметров конфигурации.
     *
     * @param ds    пул соединений с базой данных
     * @param props объект конфигурации, содержащий путь к changelog-файлу
     * @throws RuntimeException при ошибках выполнения миграций
     */
    private static void runMigrations(HikariDataSource ds, Properties props) {
        try {
            new liquibase.command.CommandScope("update")
                    .addArgumentValue("changeLogFile", props.getProperty(ConfigKeys.LIQUIBASE_CHANGELOG))
                    .addArgumentValue("url", ds.getJdbcUrl())
                    .addArgumentValue("username", ds.getUsername())
                    .addArgumentValue("password", ds.getPassword())
                    .addArgumentValue("defaultSchemaName", props.getProperty(ConfigKeys.LIQUIBASE_DEFAULT_SCHEMA))
                    .addArgumentValue("liquibaseSchemaName", props.getProperty(ConfigKeys.LIQUIBASE_SERVICE_SCHEMA))
                    .execute();

            System.out.println("Liquibase-миграции успешно применены.");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка миграции Liquibase", e);
        }
    }
}
