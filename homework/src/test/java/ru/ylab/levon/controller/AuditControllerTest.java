package ru.ylab.levon.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.service.api.AuditService;
import ru.ylab.levon.service.api.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditController.class)
class AuditControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuditService auditService;

    @MockBean
    UserService userService;

    @Test
    void getAllAuditRecords_whenCalled_returnsRecords() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);

        var firstTime = LocalDateTime.parse("2025-01-01T18:00:00");
        var secondTime = LocalDateTime.parse("2025-01-01T18:05:00");

        var firstRecord = new AuditRecord("user1", "LOGIN", firstTime);
        var secondRecord = new AuditRecord("admin", "CREATE_PRODUCT", secondTime);

        when(auditService.getAll()).thenReturn(List.of(firstRecord, secondRecord));

        mockMvc.perform(get("/api/audit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$[0].action").value("LOGIN"))
                .andExpect(jsonPath("$[0].username").value("user1"))

                .andExpect(jsonPath("$[1].action").value("CREATE_PRODUCT"))
                .andExpect(jsonPath("$[1].username").value("admin"));
    }

    @Test
    void getAllAuditRecords_whenNotLoggedIn_returns401() throws Exception {
        when(userService.isLoggedIn()).thenReturn(false);

        mockMvc.perform(get("/api/audit"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("You must be logged in"));
    }
}
