package ru.ylab.levon;

import java.util.List;

import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Product;
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

/**
 * Главный класс приложения Product Catalog Service.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Загрузку данных из хранилища;</li>
 *     <li>Создание репозиториев и сервисов;</li>
 *     <li>Инициализацию и запуск консольного интерфейса;</li>
 *     <li>Регистрацию механизма автосохранения при завершении работы приложения.</li>
 * </ul>
 * <p>
 * Если список пользователей пуст, создаётся администратор по умолчанию.
 */
public class Main {

    /**
     * Точка входа в приложение.
     * <p>
     * Выполняет инициализацию всех компонентов системы,
     * подготавливает хранилище данных и запускает консольное меню.
     *
     * @param args аргументы командной строки (не используются)
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
