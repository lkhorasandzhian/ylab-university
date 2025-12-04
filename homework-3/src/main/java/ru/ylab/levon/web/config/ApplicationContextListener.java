package ru.ylab.levon.web.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import com.zaxxer.hikari.HikariDataSource;
import org.aspectj.lang.Aspects;
import ru.ylab.levon.aspect.AuditAspect;
import ru.ylab.levon.config.*;

/**
 * Инициализатор веб-приложения.
 * <p>
 * Отвечает только за оркестровку запуска:
 * <ul>
 *     <li>Загрузка конфигурации,</li>
 *     <li>Создание DataSource,</li>
 *     <li>Применение миграций,</li>
 *     <li>Инициализацию контекста приложения (репозитории, сервисы),</li>
 *     <li>Регистрацию аспектов,</li>
 *     <li>Помещение сервисов в ServletContext.</li>
 * </ul>
 * <p>
 * Вся функциональная логика вынесена в отдельные классы:
 * ConfigLoader, DataSourceFactory, MigrationRunner, AppContextFactory.
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext ctx = sce.getServletContext();

        // 1. Загрузка конфигурации.
        ConfigLoader configLoader = new ConfigLoader();
        var props = configLoader.load("application.properties");

        // 2. Инициализация DataSource.
        DataSourceFactory dsFactory = new DataSourceFactory();
        dataSource = dsFactory.create(props);

        // 3. Запуск Liquibase миграций.
        MigrationRunner migrationRunner = new MigrationRunner();
        migrationRunner.run(dataSource, props);

        // 4. Создание всех репозиториев, сервисов, кешей.
        AppContextFactory contextFactory = new AppContextFactory();
        AppContext appContext = contextFactory.create(dataSource);

        // 5. Интеграция DI → AspectJ.
        AuditAspect auditAspect = Aspects.aspectOf(AuditAspect.class);
        auditAspect.init(appContext.auditService(), appContext.userService());

        // 6. Создание администратора при первом запуске.
        System.out.println("Creating Administrator...");
        if (appContext.userService().registerAdmin()) {
            System.out.println("Administrator has been created.");
        } else {
            System.out.println("Administrator already exists.");
        }

        // 7. Регистрируем сервисы в ServletContext.
        ctx.setAttribute("userService", appContext.userService());
        ctx.setAttribute("catalogService", appContext.catalogService());
        ctx.setAttribute("auditService", appContext.auditService());

        System.out.println("=== Application initialized successfully ===");
    }

    /**
     * Освобождает DataSource по завершении работы приложения.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("HikariCP DataSource closed.");
        }
    }
}
