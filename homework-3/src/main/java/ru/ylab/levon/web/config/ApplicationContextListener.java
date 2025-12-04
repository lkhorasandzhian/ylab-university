package ru.ylab.levon.web.config;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.command.CommandScope;
import org.aspectj.lang.Aspects;
import ru.ylab.levon.aspect.AuditAspect;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.jdbc.JdbcAuditRepository;
import ru.ylab.levon.repository.jdbc.JdbcProductRepository;
import ru.ylab.levon.repository.jdbc.JdbcUserRepository;
import ru.ylab.levon.service.*;

/**
 * Инициализатор контекста веб-приложения.
 * <p>
 * Загружает конфигурацию, создаёт DataSource, применяет миграции,
 * инициализирует сервисы и сохраняет их в {@link ServletContext}
 * для использования сервлетами.
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private HikariDataSource dataSource;

    /**
     * Запускается при старте веб-приложения.
     * <p>
     * Создаёт все необходимые инфраструктурные компоненты:
     * репозитории, сервисы, кеши и выполняет миграции базы данных.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        Properties props = loadProperties("application.properties");

        dataSource = initDataSource(props);

        runMigrations(dataSource, props);

        JdbcUserRepository userRepo = new JdbcUserRepository(dataSource);
        JdbcProductRepository productRepo = new JdbcProductRepository(dataSource);
        JdbcAuditRepository auditRepo = new JdbcAuditRepository(dataSource);

        CacheService<String, List<Product>> cacheService = new CacheService<>(20);
        AuditService auditService = new AuditService(auditRepo);

        UserService userService = new UserService(userRepo, auditService);
        CatalogService catalogService = new CatalogService(productRepo, cacheService, auditService, userService);

        AuditAspect auditAspect = Aspects.aspectOf(AuditAspect.class);
        auditAspect.init(auditService, userService);

        System.out.println("Creating Administator...");
        if (userService.registerAdmin()) {
            System.out.println("Administator has been created.");
        } else {
            System.out.println("Administator already exists.");
        }

        ctx.setAttribute("userService", userService);
        ctx.setAttribute("catalogService", catalogService);
        ctx.setAttribute("auditService", auditService);

        System.out.println("=== Application initialized successfully ===");
    }

    /**
     * Завершает работу приложения.
     * <p>
     * Освобождает ресурсы пула соединений HikariCP.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("HikariCP DataSource closed.");
        }
    }

    /**
     * Загружает конфигурационный файл *.properties* из classpath.
     *
     * @param file название конфигурационного файла
     * @return объект {@link Properties} с параметрами
     */
    private Properties loadProperties(String file) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new RuntimeException("Config not found: " + file);
            }
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    /**
     * Создаёт и настраивает пул соединений HikariCP.
     *
     * @param props конфигурационные свойства
     * @return настроенный {@link HikariDataSource}
     */
    private HikariDataSource initDataSource(Properties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setDriverClassName("org.postgresql.Driver");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }

    /**
     * Применяет Liquibase-миграции к базе данных.
     *
     * @param ds    источник соединений
     * @param props конфигурация приложения
     */
    private void runMigrations(HikariDataSource ds, Properties props) {
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
