package ru.ylab.levon.web.servlet;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.dto.UserLoginDto;
import ru.ylab.levon.service.UserService;
import ru.ylab.levon.web.util.JsonUtils;
import ru.ylab.levon.web.util.ValidatorUtils;

/**
 * Сервлет, реализующий операции аутентификации и регистрации пользователей.
 * <p>
 * Поддерживаемые маршруты:
 * <ul>
 *     <li><b>POST /auth/login</b> — вход пользователя;</li>
 *     <li><b>POST /auth/register</b> — регистрация нового пользователя;</li>
 *     <li><b>POST /auth/logout</b> — выход из системы.</li>
 * </ul>
 * Все запросы принимают и возвращают JSON.
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/*"})
public class AuthServlet extends HttpServlet {
    private UserService userService;

    /**
     * Инициализирует сервис аутентификации,
     * получая его из {@link jakarta.servlet.ServletContext}.
     */
    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute("userService");
    }

    /**
     * Обрабатывает POST-запросы на маршрутах авторизации, регистрации и выхода.
     *
     * <p>Поддерживаемые эндпоинты:</p>
     * <ul>
     *   <li><b>POST /login</b> — авторизация пользователя;</li>
     *   <li><b>POST /register</b> — регистрация нового пользователя;</li>
     *   <li><b>POST /logout</b> — завершение пользовательской сессии.</li>
     * </ul>
     *
     * @param req  HTTP-запрос, содержащий JSON с учетными данными пользователя
     *             или информацией для регистрации
     * @param resp HTTP-ответ, содержащий результат операции или сообщение об ошибке
     * @throws IOException если возникает ошибка при обработке запроса или записи ответа
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Unknown path"));
            return;
        }

        switch (path) {
            case "/login" -> handleLogin(req, resp);
            case "/register" -> handleRegister(req, resp);
            case "/logout" -> handleLogout(resp);
            default -> JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Unknown endpoint"));
        }
    }

    /**
     * Обрабатывает вход пользователя по DTO логина.
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserLoginDto dto = JsonUtils.readJson(req, UserLoginDto.class);

        var violations = ValidatorUtils.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", message));
            return;
        }

        boolean ok = userService.login(dto.username(), dto.password());

        if (!ok) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED, Map.of("error", "Invalid credentials"));
            return;
        }

        JsonUtils.writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "logged_in"));
    }

    /**
     * Обрабатывает регистрацию нового пользователя.
     */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserCreateDto dto = JsonUtils.readJson(req, UserCreateDto.class);

        var violations = ValidatorUtils.validate(dto);

        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", message));
            return;
        }

        boolean ok = userService.register(dto);

        if (!ok) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_CONFLICT, Map.of("error", "User already exists"));
            return;
        }

        JsonUtils.writeJson(resp, HttpServletResponse.SC_CREATED, Map.of("status", "created"));
    }

    /**
     * Обрабатывает выход из аккаунта пользователя.
     */
    private void handleLogout(HttpServletResponse resp) throws IOException {
        userService.logout();
        JsonUtils.writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "logged_out"));
    }
}
