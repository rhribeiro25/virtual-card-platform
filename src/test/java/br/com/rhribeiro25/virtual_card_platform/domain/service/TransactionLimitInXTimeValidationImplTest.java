
package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TransactionLimitInXTimeValidationImplTest {

    private TransactionUsecase transactionUsecase;
    private TransactionLimitInXTimeValidationImpl validation;
    private Card card;

    @BeforeEach
    void setUp() {
        transactionUsecase = mock(TransactionUsecase.class);
        validation = new TransactionLimitInXTimeValidationImpl(transactionUsecase);

        card = new Card.Builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @DisplayName("Should throw BadRequestException when limit is exceeded")
    void shouldThrowExceptionWhenLimitExceeded() {
        try (MockedStatic<MessageUtil> mocked = mockStatic(MessageUtil.class)) {
            mocked.when(() -> MessageUtil.getMessage("card.spend.limitPerMinute")).thenReturn("5");
            mocked.when(() -> MessageUtil.getMessage(eq("card.spend.rateLimit"), any())).thenReturn("Rate limit exceeded");

            when(transactionUsecase.countRecentSpends(any(), any())).thenReturn(5L);

            assertThrows(BadRequestException.class, () ->
                    validation.validate(card, BigDecimal.TEN, TransactionType.SPEND));
        }
    }

    @Test
    @DisplayName("Should not throw exception when limit not exceeded")
    void shouldNotThrowExceptionWhenWithinLimit() {
        try (MockedStatic<MessageUtil> mocked = mockStatic(MessageUtil.class)) {
            mocked.when(() -> MessageUtil.getMessage("card.spend.limitPerMinute")).thenReturn("5");

            when(transactionUsecase.countRecentSpends(any(), any())).thenReturn(3L);

            assertDoesNotThrow(() ->
                    validation.validate(card, BigDecimal.TEN, TransactionType.SPEND));
        }
    }
}
