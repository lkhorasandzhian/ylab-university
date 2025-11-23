package ru.ylab.levon.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.service.UserService;

@Aspect
public class AuditAspect {

    private static AuditService auditService;
    private static UserService userService;

    public static void init(AuditService a, UserService u) {
        auditService = a;
        userService = u;
    }

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
