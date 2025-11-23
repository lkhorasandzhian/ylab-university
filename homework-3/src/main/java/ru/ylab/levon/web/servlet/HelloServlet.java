package ru.ylab.levon.web.servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.levon.web.util.JsonUtils;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        record TestDto(String status, LocalDateTime time) {}

        TestDto dto = new TestDto("ok", LocalDateTime.now());

        JsonUtils.writeJson(resp, dto);
    }
}
