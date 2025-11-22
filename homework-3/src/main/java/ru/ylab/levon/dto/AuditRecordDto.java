package ru.ylab.levon.dto;

import java.time.LocalDateTime;

public record AuditRecordDto(
        Long id,
        String username,
        String action,
        LocalDateTime timestamp
) {}
