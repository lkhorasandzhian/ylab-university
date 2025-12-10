package ru.ylab.levon.audit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Помечает методы, действия которых должны быть записаны в аудит.
 * <p>
 * Используется аспектом для автоматического логирования операций пользователя.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audit {
    /**
     * Описание выполняемого действия, которое попадёт в запись аудита.
     */
    String value();
}
