package ru.ylab.levon.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import ru.ylab.levon.annotation.Audit;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.service.UserService;

/**
 * Аспект для автоматической записи аудита методов, помеченных аннотацией {@link Audit}.
 * <p>
 * Перехватывает вызов метода и фиксирует действие текущего пользователя.
 */
@Aspect
public class AuditAspect {
    private AuditService auditService;
    private UserService userService;

    /**
     * Инициализирует аспекты нужными сервисами.
     *
     * @param a сервис аудита
     * @param u сервис пользователей (для получения текущего пользователя)
     */
    public void init(AuditService a, UserService u) {
        auditService = a;
        userService = u;
    }

    /**
     * Выполняет аудит перед вызовом метода, помеченного {@link Audit}.
     *
     * @param jp    точка соединения, содержащая информацию о вызове
     * @param audit аннотация с описанием действия
     */
    @Before("@annotation(audit)")
    public void audit(JoinPoint jp, Audit audit) {
        if (userService.getCurrentUser() == null) {
            return;
        }

        auditService.log(
                userService.getCurrentUser().getUsername(),
                audit.value()
        );
    }
}
