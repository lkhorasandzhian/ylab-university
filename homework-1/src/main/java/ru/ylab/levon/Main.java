package ru.ylab.levon;

import ru.ylab.levon.model.User;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.service.CatalogService;
import ru.ylab.levon.service.UserService;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.storage.DataStorage;
import ru.ylab.levon.view.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        var storage = new DataStorage();

        var catalogService = new CatalogService(storage.loadProducts());
        var userService = new UserService(storage.loadUsers());
        var auditService = new AuditService(storage.loadAudit());

        if (userService.getAllUsers().isEmpty()) {
            userService.register(new User("admin", "admin", Role.ADMIN));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            storage.saveData(catalogService.getAllProducts(),
                    userService.getAllUsers(),
                    auditService.getAll());
            System.out.println("\nДанные успешно сохранены перед завершением.");
        }));

        var menu = new ConsoleMenu(catalogService, userService, auditService);
        menu.run();
    }
}
