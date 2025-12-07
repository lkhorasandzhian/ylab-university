package ru.ylab.levon.logging;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(LoggingAutoConfiguration.class)
public @interface EnableLogging {
}
