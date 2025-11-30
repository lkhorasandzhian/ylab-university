package ru.ylab.levon.web.config;

import java.util.Properties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.ylab.levon.config.AppConfig;
import ru.ylab.levon.config.ConfigLoader;

/**
 * Инициализатор контекста веб-приложения.
 * <p>
 * Загружает конфигурацию, создаёт DataSource, применяет миграции,
 * инициализирует сервисы и сохраняет их в {@link ServletContext}
 * для использования сервлетами.
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {
    /**
     * Запускается при старте веб-приложения.
     * <p>
     * Создаёт все необходимые инфраструктурные компоненты:
     * репозитории, сервисы, кеши и выполняет миграции базы данных.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("\n=== Product Catalog Service ===\n");

        ServletContext ctx = sce.getServletContext();

        Properties props = ConfigLoader.load("application.properties");

        AppConfig config = new AppConfig(props);

        ctx.setAttribute("appConfig", config);
        ctx.setAttribute("userService", config.userService());
        ctx.setAttribute("productService", config.productService());
        ctx.setAttribute("auditService", config.auditService());

        System.out.println("=== Application initialized successfully ===");
    }

    /**
     * Завершает работу приложения.
     * <p>
     * Освобождает ресурсы пула соединений HikariCP.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        AppConfig config = (AppConfig) sce.getServletContext().getAttribute("appConfig");
        if (config != null) {
            config.shutdown();
        }
    }
}
