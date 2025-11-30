package ru.ylab.levon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ylab.levon.mapper.AuditMapper;
import ru.ylab.levon.service.api.AuditService;

import java.util.List;

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

    /**
     * Возвращает список всех записей аудита.
     *
     * @return JSON-массив записей
     */
    @GetMapping
    public ResponseEntity<List<?>> getAllAuditRecords() {
        var records = auditService.getAll();
        var dtoList = AuditMapper.INSTANCE.toDtoList(records);
        return ResponseEntity.ok(dtoList);
    }
}
