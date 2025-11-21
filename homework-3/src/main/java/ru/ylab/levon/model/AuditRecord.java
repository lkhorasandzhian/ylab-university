package ru.ylab.levon.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;

@Getter
@ToString
public class AuditRecord {
    private Long id;
    private final @NonNull String username;
    private final @NonNull String action;
    private final @NonNull LocalDateTime timestamp;

    public AuditRecord(@NonNull String username, @NonNull String action, @NonNull LocalDateTime timestamp) {
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }

    public AuditRecord(@NonNull String username, @NonNull String action) {
        this(username, action, LocalDateTime.now());
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Существующий ID не может быть изменён");
        }
        this.id = id;
    }
}
