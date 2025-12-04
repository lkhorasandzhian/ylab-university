package ru.ylab.levon.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Фабрика для получения сконфигурированного {@link ObjectMapper}.
 * <p>
 * Используется для сериализации/десериализации JSON в сервлетах.
 */
public final class ObjectMapperFactory {
    /**
     * Единственный экземпляр {@link ObjectMapper}, настроенный для поддержки Java Time API
     * и человекочитаемого формата дат.
     */
    private static final ObjectMapper INSTANCE = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ObjectMapperFactory() {}

    /**
     * Возвращает заранее сконфигурированный Singleton-экземпляр {@link ObjectMapper}.
     *
     * @return общий для всего приложения ObjectMapper
     */
    public static ObjectMapper get() {
        return INSTANCE;
    }
}
