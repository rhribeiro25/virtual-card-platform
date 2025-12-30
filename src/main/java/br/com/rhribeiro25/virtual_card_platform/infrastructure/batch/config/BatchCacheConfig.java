package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchCacheConfig {
    @Bean
    @JobScope
    public BatchEntityCache<String, Card> cardCache() {
        return new BatchEntityCache<>();
    }
}
