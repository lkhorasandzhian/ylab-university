package ru.ylab.levon.view;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.model.User;
import ru.ylab.levon.service.*;

public class ConsoleMenu {
    private final CatalogService catalogService;
    private final UserService userService;
    private final AuditService auditService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(CatalogService catalogService, UserService userService, AuditService auditService) {
        this.catalogService = catalogService;
        this.userService = userService;
        this.auditService = auditService;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while (true) {
            if (!userService.isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n=== Авторизация ===");
        String username = readString("Логин: ");
        String password = readString("Пароль: ");

        if (userService.login(username, password)) {
            System.out.println("Успешный вход. \nПривет, " + username + "!");
            auditService.log(username, "Вход в систему");
        } else {
            System.out.println("Неверное имя пользователя или пароль");
        }
    }

    private void showMainMenu() {
        User current = userService.getCurrentUser();
        System.out.println("\n=== Главное меню ===");
        System.out.println("Текущий пользователь: " + current.getUsername() + " (" + current.getRole() + ")");
        System.out.println("""
                    1. Просмотреть товары
                    2. Добавить товар
                    3. Изменить товар
                    4. Удалить товар
                    5. Поиск/фильтрация
                    6. Просмотреть аудит (только ADMIN)
                    7. Выйти из системы
                    0. Завершить работу
                """);

        System.out.print("Выбор: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> listProducts();
            case "2" -> addProduct();
            case "3" -> updateProduct();
            case "4" -> removeProduct();
            case "5" -> searchProducts();
            case "6" -> showAudit();
            case "7" -> logout();
            case "0" -> exit();
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void listProducts() {
        var products = catalogService.getAllProductsCollection();
        if (products.isEmpty()) {
            System.out.println("Каталог пуст.");
        } else {
            products.forEach(System.out::println);
        }
    }

    private void addProduct() {
        if (!userService.isAdmin()) {
            System.out.println("Только администратор может добавлять товары.");
            return;
        }

        System.out.println("=== Добавление товара ===");
        String name = readString("Название: ");
        String category = readString("Категория: ");
        String brand = readString("Бренд: ");
        double price = readDouble("Цена: ");
        System.out.print("Описание (можно пустое): ");
        String description = scanner.nextLine();

        Product product = new Product(UUID.randomUUID().toString(), name, category, brand, price,
                description.isBlank() ? null : description);
        catalogService.addProduct(product);

        auditService.log(userService.getCurrentUser().getUsername(), "Добавлен товар: " + name);
        System.out.println("Товар добавлен.");
    }

    private void updateProduct() {
        if (!userService.isAdmin()) {
            System.out.println("Только администратор может изменять товары.");
            return;
        }

        String id = readString("Введите ID товара: ");

        Product product = catalogService.getProduct(id);
        if (product == null) {
            System.out.println("Товар не найден.");
            return;
        }

        System.out.println("Текущие данные: " + product);
        String name = readOptionalString("Новое название (Enter — без изменений): ");
        String category = readOptionalString("Новая категория (Enter — без изменений): ");
        String brand = readOptionalString("Новый бренд (Enter — без изменений): ");
        Double price = readOptionalDouble("Новая цена (Enter — без изменений): ");
        String description = readOptionalString("Новое описание (Enter — без изменений): ");

        boolean isUpdated = catalogService.updateProduct(id, name, category, brand, price, description);

        auditService.log(userService.getCurrentUser().getUsername(), "Изменён товар: " + id);
        System.out.println(isUpdated ? "Товар обновлён." : "Товар без изменений.");
    }

    private void removeProduct() {
        if (!userService.isAdmin()) {
            System.out.println("Только администратор может удалять товары.");
            return;
        }

        String id = readString("Введите ID товара для удаления: ");

        catalogService.removeProduct(id);

        auditService.log(userService.getCurrentUser().getUsername(), "Удалён товар: " + id);
        System.out.println("Товар удалён.");
    }

    private void searchProducts() {
        System.out.println("""
                === Поиск / фильтрация ===
                1. По категории
                2. По бренду
                3. По диапазону цен
                4. По ключевому слову
                """);
        System.out.print("Выбор: ");
        String choice = scanner.nextLine();

        List<Product> result = List.of();
        switch (choice) {
            case "1" -> {
                System.out.print("Категория: ");
                String category = scanner.nextLine();
                result = catalogService.findByCategory(category);
            }
            case "2" -> {
                System.out.print("Бренд: ");
                String brand = scanner.nextLine();
                result = catalogService.findByBrand(brand);
            }
            case "3" -> {
                double min = readDouble("Мин. цена: ");
                double max = readDouble("Макс. цена: ");
                result = catalogService.findByPriceRange(min, max);
            }
            case "4" -> {
                System.out.print("Ключевое слово: ");
                String keyword = scanner.nextLine();
                result = catalogService.search(keyword);
            }
            default -> System.out.println("Неверный выбор.");
        }

        if (result.isEmpty()) {
            System.out.println("Ничего не найдено.");
        } else {
            result.forEach(System.out::println);
        }
    }

    private void showAudit() {
        if (!userService.isAdmin()) {
            System.out.println("Доступ запрещён. Только для ADMIN.");
            return;
        }

        System.out.println("=== Аудит действий ===");
        auditService.getAll().forEach(System.out::println);
    }

    private void logout() {
        auditService.log(userService.getCurrentUser().getUsername(), "Выход из системы");
        userService.logout();
        System.out.println("Вы вышли из системы.");
    }

    private void exit() {
        System.out.println("Завершение работы программы.");
        System.exit(0);
    }

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("Ошибка: значение не может быть пустым. Повторите ввод.");
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            }
        }
    }

    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isBlank() ? null : input;
    }

    @SuppressWarnings("SameParameterValue")
    private Double readOptionalDouble(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();

        if (input.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введено некорректное число. Значение будет пропущено.");
            return null;
        }
    }
}
