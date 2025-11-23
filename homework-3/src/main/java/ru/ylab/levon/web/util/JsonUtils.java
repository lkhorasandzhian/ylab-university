package ru.ylab.levon.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class JsonUtils {
    private static final ObjectMapper mapper = ObjectMapperFactory.get();

    private JsonUtils() {}

    public static <T> T readJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        return mapper.readValue(req.getInputStream(), clazz);
    }

    public static void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        mapper.writeValue(resp.getWriter(), obj);
    }

    public static void writeJson(HttpServletResponse resp, int status, Object obj) throws IOException {
        resp.setStatus(status);
        writeJson(resp, obj);
    }
}
