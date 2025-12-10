package ru.ylab.levon.exception;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * <p>
 * Обеспечивает единый формат ошибок во всём приложении, возвращая
 * объекты {@link ApiError} с информацией о типе ошибки,
 * сообщением, статусом и URI запроса.
 *
 * <p>Обрабатывает:
 * <ul>
 *     <li>Ошибки валидации {@code @Valid};</li>
 *     <li>{@link IllegalArgumentException} из сервисов;</li>
 *     <li>Ошибки некорректных типов параметров запросов;</li>
 *     <li>Все остальные необработанные исключения (fallback).</li>
 * </ul>
 *
 * <p>
 * Использование {@code @RestControllerAdvice} позволяет автоматически
 * применять обработчики ко всем REST-контроллерам приложения.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("${app.debug:false}")
    private boolean debug;

    /**
     * Формирует объект {@link ApiError} и оборачивает его в {@link ResponseEntity}
     * с заданным HTTP-статусом.
     *
     * @param req     HTTP-запрос, в котором произошла ошибка
     * @param status  HTTP-статус ответа
     * @param message человекочитаемое описание ошибки
     * @param details дополнительные сведения (например, список ошибок валидации)
     * @return сформированный ответ с ошибкой
     */
    private ResponseEntity<ApiError> build(
            HttpServletRequest req,
            HttpStatus status,
            String message,
            List<String> details
    ) {
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                details
        );

        return new ResponseEntity<>(apiError, status);
    }

    /**
     * Упрощённая версия {@link #build(HttpServletRequest, HttpStatus, String, List)}
     * для ошибок без дополнительных деталей.
     *
     * @param req     HTTP-запрос
     * @param status  HTTP-статус
     * @param message текст ошибки
     * @return ответ с ошибкой
     */
    private ResponseEntity<ApiError> build(
            HttpServletRequest req,
            HttpStatus status,
            String message
    ) {
        return build(req, status, message, null);
    }

    /**
     * Обрабатывает ошибки валидации, возникающие при использовании {@code @Valid}.
     * <p>
     * Собирает все сообщения об ошибках для передачи в поле {@code details}.
     *
     * @param ex  исключение валидации
     * @param req HTTP-запрос
     * @return ответ 400 BAD_REQUEST с подробностями ошибок
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return build(req, HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    /**
     * Обрабатывает ошибки, связанные с некорректными аргументами бизнес-логики,
     * выбрасываемые сервисами, например {@link IllegalArgumentException}.
     *
     * @param ex  исключение
     * @param req HTTP-запрос
     * @return ответ 400 BAD_REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest req
    ) {
        return build(req, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Обрабатывает ошибки несовместимости типов, возникающие при передаче
     * параметров запроса неправильного формата
     * (например, {@code id=abc} вместо числа).
     *
     * @param ex  исключение
     * @param req HTTP-запрос
     * @return ответ 400 BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest req
    ) {
        return build(req, HttpStatus.BAD_REQUEST, "Invalid parameter type");
    }

    /**
     * Универсальный обработчик для всех необработанных исключений.
     * <p>
     * Является fallback-механизмом и предотвращает утечку деталей
     * внутренних ошибок клиенту.
     *
     * @param ex  возникшее исключение
     * @param req HTTP-запрос
     * @return ответ 500 INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOtherExceptions(
            Exception ex,
            HttpServletRequest req
    ) {
        List<String> details = null;

        if (debug) {
            details = Arrays.stream(ex.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList();
        }

        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", details);
    }
}
