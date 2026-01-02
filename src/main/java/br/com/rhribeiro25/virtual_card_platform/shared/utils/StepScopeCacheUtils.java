package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import br.com.rhribeiro25.virtual_card_platform.application.dto.AuditImport;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StepScopeCacheUtils {

    @Bean
    @StepScope
    public Map<String, AuditImport> auditCache() {
        return new HashMap<>();
    }
}
