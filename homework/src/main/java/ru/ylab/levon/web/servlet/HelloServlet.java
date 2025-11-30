package ru.ylab.levon.web.servlet;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.levon.web.util.JsonUtils;

/**
 * Тестовый сервлет, возвращающий простой JSON-ответ для проверки работоспособности приложения.
 * <p>
 * Доступен по адресу {@code GET /hello}.
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    /**
     * Возвращает JSON с текущим временем и статусом.
     *
     * @param req  входящий HTTP-запрос
     * @param resp HTTP-ответ с JSON-данными
     * @throws IOException при ошибке записи JSON
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        record TestDto(String status, LocalDateTime time) {}

        TestDto dto = new TestDto("ok", LocalDateTime.now());

        JsonUtils.writeJson(resp, dto);
    }
}
