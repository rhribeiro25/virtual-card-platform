package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BatchCacheConfig {
    @Bean
    @JobScope
    public Map<String, Provider> providerCache() {
        return new HashMap<>();
    }

    @Bean
    @JobScope
    public Map<String, Card> cardCache() {
        return new HashMap<>();
    }

    @Bean
    @JobScope
    public Map<String, Transaction> transactionCache() {
        return new HashMap<>();
    }

}
