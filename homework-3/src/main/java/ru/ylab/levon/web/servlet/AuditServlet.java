package ru.ylab.levon.web.servlet;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.levon.mapper.AuditMapper;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.web.util.JsonUtils;

@WebServlet(name = "AuditServlet", urlPatterns = {"/audit/*"})
public class AuditServlet extends HttpServlet {
    private AuditService auditService;

    @Override
    public void init() {
        auditService = (AuditService) getServletContext().getAttribute("auditService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            handleGetAll(resp);
            return;
        }

        JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                Map.of("error", "Unknown audit endpoint"));
    }

    private void handleGetAll(HttpServletResponse resp) throws IOException {
        var records = auditService.getAll();
        var dtoList = AuditMapper.INSTANCE.toDtoList(records);
        JsonUtils.writeJson(resp, dtoList);
    }
}
