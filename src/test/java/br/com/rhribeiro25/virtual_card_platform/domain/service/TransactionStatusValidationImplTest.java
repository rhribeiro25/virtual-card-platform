
package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class TransactionStatusValidationImplTest {

    private final TransactionStatusValidationImpl validation = new TransactionStatusValidationImpl();

    @BeforeAll
    static void setup() {
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("card.blocked.message", null, Locale.getDefault()))
                .thenReturn("Card is blocked");

        MessageUtils.setMessageSource(messageSource);
    }

    @Test
    @DisplayName("Should throw BadRequestException when card is blocked")
    void shouldThrowWhenCardBlocked() {
        Card card = new Card.Builder()
                .cardholderName("Blocked User")
                .balance(BigDecimal.TEN)
                .status(CardStatus.BLOCKED)
                .build();
        Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.valueOf(10)).type(TransactionType.SPEND).build();
        assertThrows(BadRequestException.class, () ->
                validation.validate(transaction));
    }

    @Test
    @DisplayName("Should not throw when card is active")
    void shouldNotThrowWhenCardIsActive() {
        Card card = new Card.Builder()
                .cardholderName("Active User")
                .balance(BigDecimal.TEN)
                .status(CardStatus.ACTIVE)
                .build();
        Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.valueOf(10)).type(TransactionType.SPEND).build();
        assertDoesNotThrow(() ->
                validation.validate(transaction));
    }

    @Test
    @DisplayName("Should support only SPEND and TOPUP transaction types")
    void shouldSupportSpecificTransactionTypes() {
        assertTrue(validation.supports(TransactionType.SPEND));
        assertTrue(validation.supports(TransactionType.TOPUP));
    }

    @Test
    @DisplayName("Should not support transaction TRANSFER ")
    void shouldNotSupportOtherTransactionTypes() {
        assertFalse(validation.supports(TransactionType.TRANSFER));
    }

}
