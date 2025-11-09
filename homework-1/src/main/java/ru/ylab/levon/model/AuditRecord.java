package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public record AuditRecord(String username, String action, LocalDateTime timestamp) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public AuditRecord(String username, String action) {
        this(username, action, LocalDateTime.now());
    }
}
