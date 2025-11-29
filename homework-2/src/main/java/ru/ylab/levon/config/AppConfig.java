package ru.ylab.levon.config;

import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

import ru.ylab.levon.config.factory.RepositoryFactory;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.service.*;
import ru.ylab.levon.repository.api.*;

public class AppConfig {
    private final Properties props;
    private final DataSource dataSource;

    public AppConfig(Properties props) {
        this.props = props;
        this.dataSource = DatabaseConfig.createDataSource(props);
        MigrationConfig.runMigrations(props);
    }

    public ProductRepository productRepository() {
        return RepositoryFactory.createProductRepository(props, dataSource);
    }

    public UserRepository userRepository() {
        return RepositoryFactory.createUserRepository(props, dataSource);
    }

    public AuditRepository auditRepository() {
        return RepositoryFactory.createAuditRepository(props, dataSource);
    }

    public CatalogService catalogService() {
        return new CatalogService(productRepository(), cacheService());
    }

    public CacheService<String, List<Product>> cacheService() {
        return new CacheService<>(20);
    }

    public UserService userService() {
        return new UserService(userRepository());
    }

    public AuditService auditService() {
        return new AuditService(auditRepository());
    }
}
