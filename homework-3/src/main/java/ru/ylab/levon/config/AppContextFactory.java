package ru.ylab.levon.config;

import com.zaxxer.hikari.HikariDataSource;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.jdbc.JdbcAuditRepository;
import ru.ylab.levon.repository.jdbc.JdbcProductRepository;
import ru.ylab.levon.repository.jdbc.JdbcUserRepository;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.service.CacheService;
import ru.ylab.levon.service.CatalogService;
import ru.ylab.levon.service.UserService;

import java.util.List;

public class AppContextFactory {

    /**
     * Создаёт все репозитории, сервисы и кеши приложения.
     */
    public AppContext create(HikariDataSource ds) {

        JdbcUserRepository userRepo = new JdbcUserRepository(ds);
        JdbcProductRepository productRepo = new JdbcProductRepository(ds);
        JdbcAuditRepository auditRepo = new JdbcAuditRepository(ds);

        CacheService<String, List<Product>> cache = new CacheService<>(20);
        AuditService auditService = new AuditService(auditRepo);

        UserService userService = new UserService(userRepo, auditService);
        CatalogService catalogService =
                new CatalogService(productRepo, cache, auditService, userService);

        return new AppContext(userService, catalogService, auditService);
    }
}
