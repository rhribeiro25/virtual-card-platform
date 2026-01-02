package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JobScopeCacheUtils {

    @Bean
    public Map<String, Provider> providerCache() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Card> cardCache() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Transaction> transactionCache() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, CardProvider> cardProviderCache() {
        return new HashMap<>();
    }

    public void clearMaps(){
        providerCache().clear();
        cardCache().clear();
        transactionCache().clear();
        cardProviderCache().clear();
    }

}
