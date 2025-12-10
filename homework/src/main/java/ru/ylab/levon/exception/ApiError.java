package ru.ylab.levon.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Унифицированная модель ошибки, возвращаемая REST-контроллерами.
 * <p>
 * Используется глобальным обработчиком исключений для представления
 * информации об ошибке в стандартизованном формате.
 *
 * <p>Содержит:
 * <ul>
 *     <li>{@code timestamp} — момент возникновения ошибки;</li>
 *     <li>{@code status} — HTTP-статус (числовое значение);</li>
 *     <li>{@code error} — краткое описание типа ошибки (например, "BAD_REQUEST");</li>
 *     <li>{@code message} — человекочитаемое сообщение об ошибке;</li>
 *     <li>{@code path} — URI запроса, в котором произошла ошибка;</li>
 *     <li>{@code details} — список дополнительных сведений, например ошибок валидации.</li>
 * </ul>
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {

    /**
     * Конструктор для ошибок без дополнительных деталей.
     *
     * @param status  HTTP-статус ошибки
     * @param error   краткое описание типа ошибки
     * @param message подробное сообщение
     * @param path    URI запроса, вызвавшего ошибку
     */
    public ApiError(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    /**
     * Конструктор для ошибок с деталями валидации или дополнительными данными.
     *
     * @param status  HTTP-статус ошибки
     * @param error   краткое описание типа ошибки
     * @param message подробное сообщение
     * @param path    URI запроса
     * @param details список деталей ошибки
     */
    public ApiError(int status, String error, String message, String path, List<String> details) {
        this(LocalDateTime.now(), status, error, message, path, details);
    }
}
