
package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TransactionDuplicationBetweenRangeTimeValidationImplTest {

    @Mock
    private TransactionUsecase transactionUsecase;

    @InjectMocks
    private TransactionDuplicationBetweenRangeTimeValidationImpl validation;

    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        card = new Card.Builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @DisplayName("Should delegate duplication check to TransactionUsecase")
    void shouldCallTransactionUsecaseForDuplicationCheck() {
        Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.valueOf(10)).type(TransactionType.SPEND).build();
        validation.validate(transaction);

        verify(transactionUsecase, times(1))
                .isDuplicateTransaction(card, BigDecimal.TEN, TransactionType.SPEND);
    }

    @Test
    @DisplayName("Should throw BadRequestException when duplicate transaction detected")
    void shouldThrowExceptionWhenDuplicateTransactionDetected() {
        doThrow(new BadRequestException("duplicate"))
                .when(transactionUsecase).isDuplicateTransaction(card, BigDecimal.TEN, TransactionType.SPEND);
        Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.valueOf(10)).type(TransactionType.SPEND).build();
        assertThrows(BadRequestException.class, () ->
                validation.validate(transaction));
    }

    @Test
    @DisplayName("Should not support SPEND and TOPUP transaction types")
    void shouldSupportSpecificTransactionTypes() {
        assertFalse(validation.supports(TransactionType.SPEND));
        assertFalse(validation.supports(TransactionType.TOPUP));
    }

    @Test
    @DisplayName("Should not support transaction TRANSFER ")
    void shouldNotSupportOtherTransactionTypes() {
        assertFalse(validation.supports(TransactionType.TRANSFER));
    }
}
