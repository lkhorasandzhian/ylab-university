package ru.ylab.levon.web.servlet;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.service.UserService;
import ru.ylab.levon.web.util.JsonUtils;

@WebServlet(name = "AuthServlet", urlPatterns = {"/api/auth/*"})
public class AuthServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // "/login" или "/register".

        if (path == null) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Unknown path"));
            return;
        }

        switch (path) {
            case "/login" -> handleLogin(req, resp);
            case "/register" -> handleRegister(req, resp);
            default -> JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Unknown endpoint"));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        record LoginDto(String username, String password) {}

        LoginDto dto = JsonUtils.readJson(req, LoginDto.class);

        boolean ok = userService.login(dto.username(), dto.password());

        if (!ok) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED, Map.of("error", "Invalid credentials"));
            return;
        }

        JsonUtils.writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "logged_in"));
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserCreateDto dto = JsonUtils.readJson(req, UserCreateDto.class);

        boolean ok = userService.register(dto);

        if (!ok) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_CONFLICT, Map.of("error", "User already exists"));
            return;
        }

        JsonUtils.writeJson(resp, HttpServletResponse.SC_CREATED, Map.of("status", "created"));
    }
}
