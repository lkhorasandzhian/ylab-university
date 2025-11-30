package ru.ylab.levon.config;

import java.util.List;
import java.util.Properties;

import com.zaxxer.hikari.HikariDataSource;
import ru.ylab.levon.config.factory.RepositoryFactory;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.service.api.AuditService;
import ru.ylab.levon.service.api.ProductService;
import ru.ylab.levon.service.api.UserService;
import ru.ylab.levon.service.impl.AuditServiceImpl;
import ru.ylab.levon.service.impl.CacheServiceImpl;
import ru.ylab.levon.service.impl.ProductServiceImpl;
import ru.ylab.levon.service.impl.UserServiceImpl;

/**
 * Конфигурационный класс приложения.
 * <p>
 * Создаёт DataSource, применяет миграции, инициализирует репозитории
 * и сервисы, а также содержит логику завершения работы (shutdown).
 */
public class AppConfig {
    private final HikariDataSource dataSource;

    private final AuditService auditService;
    private final UserService userService;
    private final ProductService productService;

    public AppConfig(Properties props) {
        this.dataSource = DatabaseConfig.createHikariDataSource(props);

        // Liquibase-миграции.
        MigrationConfig.runMigrations(props);

        // Репозитории.
        var userRepo = RepositoryFactory.createUserRepository(props, dataSource);
        var productRepo = RepositoryFactory.createProductRepository(props, dataSource);
        var auditRepo = RepositoryFactory.createAuditRepository(props, dataSource);

        // Кэширование + сервисы.
        var cacheService = new CacheServiceImpl<String, List<Product>>(20);
        this.auditService = new AuditServiceImpl(auditRepo);
        this.userService = new UserServiceImpl(userRepo, auditService);
        this.productService = new ProductServiceImpl(productRepo, cacheService, auditService, userService);

        // Добавить админа, если отсутствует.
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        System.out.println("Creating Administrator...");
        if (userService.registerAdmin()) {
            System.out.println("Administrator has been created.");
        } else {
            System.out.println("Administrator already exists.");
        }
    }

    public AuditService auditService() {
        return auditService;
    }

    public UserService userService() {
        return userService;
    }

    public ProductService productService() {
        return productService;
    }

    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
