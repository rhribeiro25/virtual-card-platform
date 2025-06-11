
package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TransactionBalanceValidationImplTest {

    private TransactionBalanceValidationImpl validation;

    private MessageSource messageSource;

    @BeforeEach
    void setup() {
        messageSource = mock(MessageSource.class);
        MessageUtil.setMessageSource(messageSource);
        when(messageSource.getMessage(eq("card.insufficientBalance"), any(), any())).thenReturn("Saldo insuficiente");
        validation = new TransactionBalanceValidationImpl();
    }

    @Test
    @DisplayName("Should throw BadRequestException when balance is insufficient")
    void shouldThrowWhenBalanceIsInsufficient() {
        Card card = new Card.Builder()
                .cardholderName("Test")
                .balance(BigDecimal.valueOf(50))
                .build();

        assertThrows(BadRequestException.class, () ->
                validation.validate(card, BigDecimal.valueOf(100), TransactionType.SPEND));
    }

    @Test
    @DisplayName("Should not throw when balance is sufficient")
    void shouldNotThrowWhenBalanceIsSufficient() {
        Card card = new Card.Builder()
                .cardholderName("Test")
                .balance(BigDecimal.valueOf(200))
                .build();

        assertDoesNotThrow(() ->
                validation.validate(card, BigDecimal.valueOf(100), TransactionType.SPEND));
    }

    @Test
    @DisplayName("Should not throw when transaction is TOPUP regardless of balance")
    void shouldNotThrowForTopUpTransaction() {
        Card card = new Card.Builder()
                .cardholderName("Test")
                .balance(BigDecimal.ZERO)
                .build();

        assertDoesNotThrow(() ->
                validation.validate(card, BigDecimal.valueOf(100), TransactionType.TOPUP));
    }
}
