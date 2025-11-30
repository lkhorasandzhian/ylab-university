package ru.ylab.levon.web.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Утилитарный класс для работы с JSON в сервлетах.
 * <p>
 * Используется для сериализации и десериализации объектов через Jackson.
 */
public final class JsonUtils {
    private static final ObjectMapper mapper = ObjectMapperFactory.get();

    private JsonUtils() {}

    /**
     * Читает JSON-тело HTTP-запроса и десериализует его в указанный класс.
     *
     * @param req   HTTP-запрос
     * @param clazz класс, в который нужно десериализовать JSON
     * @param <T>   тип результата
     * @return десериализованный объект
     * @throws IOException при ошибке чтения или разбора JSON
     */
    public static <T> T readJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        return mapper.readValue(req.getInputStream(), clazz);
    }

    /**
     * Отправляет объект клиенту в виде JSON с кодом 200 OK.
     *
     * @param resp HTTP-ответ
     * @param obj  объект для сериализации
     * @throws IOException при ошибке записи JSON
     */
    public static void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        mapper.writeValue(resp.getWriter(), obj);
    }

    /**
     * Устанавливает HTTP-статус и отправляет объект клиенту в формате JSON.
     *
     * @param resp   HTTP-ответ
     * @param status статус-код HTTP
     * @param obj    объект для сериализации
     * @throws IOException при ошибке записи JSON
     */
    public static void writeJson(HttpServletResponse resp, int status, Object obj) throws IOException {
        resp.setStatus(status);
        writeJson(resp, obj);
    }
}
