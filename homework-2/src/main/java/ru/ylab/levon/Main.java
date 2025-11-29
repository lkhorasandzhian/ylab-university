package ru.ylab.levon;

import java.util.Properties;

import ru.ylab.levon.config.AppConfig;
import ru.ylab.levon.config.ConfigLoader;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.view.ConsoleMenu;

/**
 * Точка входа в приложение Product Catalog Service.
 * <p>
 * Main отвечает только за:
 * <ul>
 *     <li>Загрузку конфигурации;</li>
 *     <li>Инициализацию контейнера зависимостей {@link AppConfig};</li>
 *     <li>Запуск пользовательского интерфейса;</li>
 * </ul>
 * Вся остальная логика инициализации вынесена в config слой.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("\n=== Product Catalog Service ===\n");

        Properties props = ConfigLoader.load("application.properties");

        AppConfig config = new AppConfig(props);

        var userService = config.userService();
        var catalogService = config.catalogService();
        var auditService = config.auditService();

        // Создание администратора по умолчанию, если база пользователей пуста.
        if (config.userRepository().findByUsername("admin") == null) {
            userService.register(new UserCreateDto("admin", "admin", Role.ADMIN));
        }

        new ConsoleMenu(catalogService, userService, auditService).run();
    }
}
