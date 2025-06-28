
package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        try (MockedStatic<MessageUtils> mocked = mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.getMessage("card.spend.limitPerMinute")).thenReturn("5");
            mocked.when(() -> MessageUtils.getMessage(eq("card.spend.rateLimit"), any())).thenReturn("Rate limit exceeded");

            when(transactionUsecase.countRecentSpends(any(), any())).thenReturn(5L);
            Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.valueOf(10)).type(TransactionType.SPEND).build();
            assertThrows(BadRequestException.class, () ->
                    validation.validate(transaction));
        }
    }

    @Test
    @DisplayName("Should not throw exception when limit not exceeded")
    void shouldNotThrowExceptionWhenWithinLimit() {
        try (MockedStatic<MessageUtils> mocked = mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.getMessage("card.spend.limitPerMinute")).thenReturn("5");

            when(transactionUsecase.countRecentSpends(any(), any())).thenReturn(3L);
            Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.valueOf(10)).type(TransactionType.SPEND).build();
            assertDoesNotThrow(() ->
                    validation.validate(transaction));
        }
    }

    @Test
    @DisplayName("Should support only SPEND and TOPUP transaction types")
    void shouldSupportSpecificTransactionTypes() {
        assertTrue(validation.supports(TransactionType.SPEND));
    }

    @Test
    @DisplayName("Should not support transaction TRANSFER ")
    void shouldNotSupportOtherTransactionTypes() {
        assertFalse(validation.supports(TransactionType.TRANSFER));
    }
}
