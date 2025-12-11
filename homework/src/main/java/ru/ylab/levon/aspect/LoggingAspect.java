package ru.ylab.levon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования времени выполнения методов сервисного слоя.
 * <p>
 * Перехватывает вызовы всех методов в пакете {@code ru.ylab.levon.service}
 * и выводит в консоль время их выполнения.
 */
@Aspect
@Component
public class LoggingAspect {
    /**
     * Логирует время выполнения метода.
     *
     * @param jp точка соединения, содержащая информацию о вызове
     * @return результат выполнения метода
     * @throws Throwable если перехватываемый метод выбрасывает исключение
     */
    @Around("execution(* ru.ylab.levon.service..*(..))")
    public Object logTime(ProceedingJoinPoint jp) throws Throwable {
        long start = System.nanoTime();

        Object result = jp.proceed();

        long end = System.nanoTime();
        long ms = (end - start) / 1_000_000;

        System.out.println("[LOG] " + jp.getSignature() + " executed in " + ms + " ms");

        return result;
    }
}
