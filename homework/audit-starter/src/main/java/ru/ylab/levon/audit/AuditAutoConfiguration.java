package ru.ylab.levon.audit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import ru.ylab.levon.audit.api.AuditHandler;
import ru.ylab.levon.audit.api.CurrentUserProvider;

@AutoConfiguration
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnBean({AuditHandler.class, CurrentUserProvider.class})
    public AuditAspect auditAspect(AuditHandler handler, CurrentUserProvider provider) {
        return new AuditAspect(handler, provider);
    }
}
