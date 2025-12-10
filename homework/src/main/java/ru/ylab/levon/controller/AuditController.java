package ru.ylab.levon.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ylab.levon.dto.AuditRecordDto;
import ru.ylab.levon.mapper.AuditMapper;
import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.service.api.AuditService;
import ru.ylab.levon.service.api.UserService;

/**
 * REST-контроллер для работы с журналом аудита.
 * <p>
 * Предоставляет эндпоинт для получения всех записей аудита.
 * Доступно только авторизованным пользователям.
 *
 * <p>Маршрут контроллера:
 * <ul>
 *   <li><b>GET /api/audit</b> — получить все записи аудита</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;
    private final UserService userService;

    /**
     * Возвращает список всех записей аудита.
     *
     * @return JSON-массив записей
     */
    @GetMapping
    public ResponseEntity<?> getAllAuditRecords() {
        if (!userService.isLoggedIn()) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "You must be logged in"));
        }

        List<AuditRecord> records = auditService.getAll();
        List<AuditRecordDto> dtoList = AuditMapper.INSTANCE.toDtoList(records);

        return ResponseEntity.ok(dtoList);
    }
}
