package ru.ylab.levon;

import ru.ylab.levon.model.User;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.service.CatalogService;
import ru.ylab.levon.service.UserService;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.view.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        var catalogService = new CatalogService();
        var userService = new UserService();
        var auditService = new AuditService();

        userService.register(new User("admin", "admin", Role.ADMIN));

        var menu = new ConsoleMenu(catalogService, userService, auditService);
        menu.run();
    }
}
