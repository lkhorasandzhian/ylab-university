package ru.ylab.levon.audit.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ylab.levon.audit.api.AuditHandler;
import ru.ylab.levon.service.api.AuditService;

@Component
@RequiredArgsConstructor
public class AuditHandlerImpl implements AuditHandler {
    private final AuditService auditService;

    @Override
    public void handle(String username, String action) {
        auditService.log(username, action);
    }
}
