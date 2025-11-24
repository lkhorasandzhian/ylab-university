package ru.ylab.levon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggingAspect {

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
