package ru.ylab.levon.model;

import java.time.LocalDateTime;

public record AuditRecord(String username, String action, LocalDateTime timestamp) {
    public AuditRecord(String username, String action) {
        this(username, action, LocalDateTime.now());
    }
}
