package ru.ylab.levon.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.model.User;
import ru.ylab.levon.service.*;

/**
 * Класс {@code ConsoleMenu} реализует консольный пользовательский интерфейс
 * для взаимодействия с приложением Product Catalog Service.
 * <p>
 * Предоставляет меню для авторизации, регистрации, работы с каталогом товаров,
 * выполнения операций CRUD, поиска и просмотра аудита.
 * В зависимости от роли пользователя (ADMIN/USER) доступен различный функционал.
 */
public class ConsoleMenu {
    private final CatalogService catalogService;
    private final UserService userService;
    private final AuditService auditService;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Создаёт консольное меню, связанное с указанными сервисами.
     *
     * @param catalogService сервис каталога товаров
     * @param userService    сервис пользователей и авторизации
     * @param auditService   сервис аудита действий
     */
    public ConsoleMenu(CatalogService catalogService, UserService userService, AuditService auditService) {
        this.catalogService = catalogService;
        this.userService = userService;
        this.auditService = auditService;
    }

    /**
     * Запускает основной цикл работы консольного интерфейса.
     * В зависимости от состояния авторизации отображает меню входа
     * или основное меню пользователя.
     */
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

    /**
     * Отображает главное меню входа с опциями:
     * вход, регистрация, завершение работы.
     */
    private void showLoginMenu() {
        System.out.println("\n=== Главное меню входа ===");
        System.out.println("""
                1. Войти
                2. Зарегистрироваться
                0. Завершить работу
                """);

        System.out.print("Выбор: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleRegistration();
            case "0" -> exit();
            default -> System.out.println("Неверный выбор! Попробуйте снова.");
        }
    }

    /**
     * Обрабатывает процесс авторизации пользователя.
     */
    private void handleLogin() {
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

    /**
     * Обрабатывает процесс регистрации нового пользователя.
     */
    private void handleRegistration() {
        System.out.println("\n=== Регистрация ===");
        String username = readString("Логин: ");
        String password = readString("Пароль: ");

        var userCreateDto = new UserCreateDto(username, password, Role.USER);

        boolean isNewUser;
        try {
            isNewUser = userService.register(userCreateDto);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка! " + e.getMessage());
            return;
        }

        if (isNewUser) {
            System.out.println("Пользователь успешно зарегистрирован.");
            auditService.log(username, "Регистрация нового пользователя");
        } else {
            System.out.println("Ошибка! Пользователь с таким именем уже существует.");
        }
    }

    /**
     * Отображает основное меню приложения для авторизованных пользователей.
     * В зависимости от роли предоставляет доступ к различным функциям.
     */
    private void showMainMenu() {
        User current = userService.getCurrentUser();
        System.out.println("\n=== Главное меню ===");
        System.out.println("Текущий пользователь: " + current.getUsername() + " (" + current.getRole() + ")");
        System.out.println("""
                    1. Просмотреть товары
                    2. Добавить товар       (только ADMIN)
                    3. Изменить товар       (только ADMIN)
                    4. Удалить товар        (только ADMIN)
                    5. Поиск/фильтрация
                    6. Просмотреть аудит    (только ADMIN)
                    7. Выйти из системы
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
            default -> System.out.println("Неверный выбор!");
        }
    }

    /**
     * Отображает все товары в каталоге.
     */
    private void listProducts() {
        var products = catalogService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Каталог пуст.");
        } else {
            products.forEach(System.out::println);
        }
    }

    /**
     * Добавляет новый товар (доступно только администратору).
     */
    private void addProduct() {
        if (!userService.isAdmin()) {
            System.out.println("Только администратор может добавлять товары.");
            return;
        }

        System.out.println("=== Добавление товара ===");
        String name = readString("Название: ");
        String category = readString("Категория: ");
        String brand = readString("Бренд: ");
        BigDecimal price = readBigDecimal("Цена: ");
        String description = readOptionalString("Описание (можно пустое): ");

        var productCreateDto = new ProductCreateDto(name, category, brand, price, description);

        try {
            catalogService.addProduct(productCreateDto);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка! " + e.getMessage());
            return;
        }

        auditService.log(userService.getCurrentUser().getUsername(), "Добавлен товар: " + name);
        System.out.println("Товар добавлен.");
    }

    /**
     * Изменяет существующий товар (доступно только администратору).
     */
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
        BigDecimal price = readOptionalBigDecimal("Новая цена (Enter — без изменений): ");
        String description = readOptionalString("Новое описание (Enter — без изменений): ");

        var productUpdateDto = new ProductUpdateDto(name, category, brand, price, description);

        boolean isUpdated;
        try {
            isUpdated = catalogService.updateProduct(id, productUpdateDto);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка! " + e.getMessage());
            return;
        }

        auditService.log(userService.getCurrentUser().getUsername(), "Изменён товар: " + id);
        System.out.println(isUpdated ? "Товар обновлён." : "Товар без изменений.");
    }

    /**
     * Удаляет товар из каталога (доступно только администратору).
     */
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

    /**
     * Выполняет поиск или фильтрацию товаров по выбранному критерию.
     */
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
                BigDecimal min = readBigDecimal("Мин. цена: ");
                BigDecimal max = readBigDecimal("Макс. цена: ");
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

    /**
     * Отображает журнал аудита действий пользователей (только для администратора).
     */
    private void showAudit() {
        if (!userService.isAdmin()) {
            System.out.println("Доступ запрещён. Только для ADMIN.");
            return;
        }

        System.out.println("=== Аудит действий ===");
        auditService.getAll().forEach(System.out::println);
    }

    /**
     * Выполняет выход из системы текущего пользователя.
     */
    private void logout() {
        auditService.log(userService.getCurrentUser().getUsername(), "Выход из системы");
        userService.logout();
        System.out.println("Вы вышли из системы.");
    }

    /**
     * Завершает работу приложения.
     */
    private void exit() {
        System.out.println("Завершение работы программы.");
        System.exit(0);
    }

    /**
     * Считывает обязательную строку из консоли.
     *
     * @param prompt приглашение для ввода
     * @return непустая строка
     */
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Считывает число с плавающей точкой из консоли.
     *
     * @param prompt приглашение для ввода
     * @return корректное значение типа {@link java.math.BigDecimal}
     */
    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введено некорректное число. Повторите попытку.");
            }
        }
    }

    /**
     * Считывает необязательную строку. Пустая строка трактуется как {@code null}.
     *
     * @param prompt приглашение для ввода
     * @return строка либо {@code null}, если введено пустое значение
     */
    private String readOptionalString(String prompt) {
        String input = readString(prompt);
        return input.isBlank() ? null : input;
    }

    /**
     * Считывает необязательное число с плавающей точкой.
     * Пустая строка трактуется как {@code null}.
     *
     * @param prompt приглашение для ввода
     * @return значение {@link java.math.BigDecimal} либо {@code null}, если введено пустое значение
     */
    @SuppressWarnings("SameParameterValue")
    private BigDecimal readOptionalBigDecimal(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(input);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка! Введено некорректное число. Значение будет пропущено.");
            return null;
        }
    }
}
