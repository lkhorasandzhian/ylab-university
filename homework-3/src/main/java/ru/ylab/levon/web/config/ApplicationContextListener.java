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
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.jdbc.JdbcAuditRepository;
import ru.ylab.levon.repository.jdbc.JdbcProductRepository;
import ru.ylab.levon.repository.jdbc.JdbcUserRepository;
import ru.ylab.levon.service.*;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        Properties props = loadProperties("application.properties");

        dataSource = initDataSource(props);

        runMigrations(dataSource, props);

        var userRepo = new JdbcUserRepository(dataSource);
        var productRepo = new JdbcProductRepository(dataSource);
        var auditRepo = new JdbcAuditRepository(dataSource);

        var cacheService = new CacheService<String, List<Product>>(20);
        var auditService = new AuditService(auditRepo);

        var userService = new UserService(userRepo, auditService);
        var catalogService = new CatalogService(productRepo, cacheService, auditService, userService);

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

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("HikariCP DataSource closed.");
        }
    }

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
