package ru.ylab.levon.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDto(
        String message,
        int status,
        LocalDateTime timestamp,
        List<String> errors
) {}
