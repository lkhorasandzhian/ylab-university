package ru.ylab.levon.web.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.jdbc.JdbcUserRepository;
import ru.ylab.levon.repository.jdbc.JdbcProductRepository;
import ru.ylab.levon.repository.jdbc.JdbcAuditRepository;
import ru.ylab.levon.service.CacheService;
import ru.ylab.levon.service.UserService;
import ru.ylab.levon.service.CatalogService;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.Main;

import java.util.List;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        var userRepo = new JdbcUserRepository(Main.dataSource);
        var productRepo = new JdbcProductRepository(Main.dataSource);
        var auditRepo = new JdbcAuditRepository(Main.dataSource);

        final int CACHE_SIZE = 20;
        var cacheService = new CacheService<String, List<Product>>(CACHE_SIZE);
        var userService = new UserService(userRepo);
        var catalogService = new CatalogService(productRepo, cacheService);
        var auditService = new AuditService(auditRepo);

        ctx.setAttribute("userService", userService);
        ctx.setAttribute("catalogService", catalogService);
        ctx.setAttribute("auditService", auditService);
    }
}
