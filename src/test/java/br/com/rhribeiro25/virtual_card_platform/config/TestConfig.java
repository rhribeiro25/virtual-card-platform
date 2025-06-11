package br.com.rhribeiro25.virtual_card_platform.config;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public CardUsecase cardUsecase() {
        return Mockito.mock(CardUsecase.class);
    }
}