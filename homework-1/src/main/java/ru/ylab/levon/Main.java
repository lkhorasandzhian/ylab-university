package ru.ylab.levon;

import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.model.User;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.repository.file.FileAuditRepository;
import ru.ylab.levon.repository.file.FileProductRepository;
import ru.ylab.levon.repository.file.FileUserRepository;
import ru.ylab.levon.service.CacheService;
import ru.ylab.levon.service.CatalogService;
import ru.ylab.levon.service.UserService;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.storage.DataStorage;
import ru.ylab.levon.view.ConsoleMenu;

import java.util.List;

/**
 * Главный класс приложения Product Catalog Service.
 * <p>
 * Отвечает за инициализацию всех основных компонентов системы:
 * <ul>
 *   <li>Загрузку сохранённых данных (товары, пользователи, аудит);</li>
 *   <li>Создание сервисов бизнес-логики;</li>
 *   <li>Инициализацию консольного интерфейса {@link ConsoleMenu};</li>
 *   <li>Регистрацию хука завершения для автоматического сохранения данных.</li>
 * </ul>
 * После запуска создаётся базовый пользователь-администратор
 * (если отсутствует сохранённый список пользователей).
 */
public class Main {
    /**
     * Точка входа в приложение.
     * <p>
     * Загружает данные, инициализирует сервисы и запускает консольное меню.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        var storage = new DataStorage();

        var productRepo = new FileProductRepository(storage.loadProducts());
        var userRepo = new FileUserRepository(storage.loadUsers());
        var auditRepo = new FileAuditRepository(storage.loadAudit());

        var cacheService = new CacheService<String, List<Product>>(20);
        var catalogService = new CatalogService(productRepo, cacheService);
        var userService = new UserService(userRepo);
        var auditService = new AuditService(auditRepo);

        // Создание администратора по умолчанию, если база пользователей пуста.
        if (userService.getStorage().isEmpty()) {
            userService.register(new UserCreateDto("admin", "admin", Role.ADMIN));
        }

        // Хук завершения: сохраняет все данные при выходе из программы.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            storage.saveData(
                    catalogService.getStorage(),
                    userService.getStorage(),
                    auditService.getStorage()
            );
            System.out.println("\nДанные успешно сохранены перед завершением.");
        }));

        // Запуск консольного интерфейса приложения.
        var menu = new ConsoleMenu(catalogService, userService, auditService);
        menu.run();
    }
}
