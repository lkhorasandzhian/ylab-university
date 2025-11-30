package ru.ylab.levon.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.ylab.levon.service.api.AuditService;
import ru.ylab.levon.service.api.UserService;

/**
 * Аспект для автоматической записи аудита методов,
 * помеченных аннотацией {@link Audit}.
 * <p>
 * Перехватывает вызов метода и фиксирует действие
 * текущего авторизованного пользователя.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditService auditService;
    private final UserService userService;

    /**
     * Выполняет запись аудита перед вызовом метода,
     * помеченного @Audit.
     *
     * @param jp    точка соединения
     * @param audit аннотация с описанием действия
     */
    @Before("@annotation(audit)")
    public void audit(JoinPoint jp, Audit audit) {
        var user = userService.getCurrentUser();
        if (user == null) {
            return; // Не логировать гостей.
        }

        auditService.log(
                user.getUsername(),
                audit.value()
        );
    }
}
