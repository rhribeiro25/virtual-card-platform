
package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TransactionDuplicationValidationImplTest {

    @Mock
    private TransactionUsecase transactionUsecase;

    @InjectMocks
    private TransactionDuplicationValidationImpl validation;

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
        validation.validate(card, BigDecimal.TEN, TransactionType.SPEND);

        verify(transactionUsecase, times(1))
                .isDuplicateTransaction(card, BigDecimal.TEN, TransactionType.SPEND);
    }

    @Test
    @DisplayName("Should throw BadRequestException when duplicate transaction detected")
    void shouldThrowExceptionWhenDuplicateTransactionDetected() {
        doThrow(new BadRequestException("duplicate"))
                .when(transactionUsecase).isDuplicateTransaction(card, BigDecimal.TEN, TransactionType.SPEND);

        assertThrows(BadRequestException.class, () ->
                validation.validate(card, BigDecimal.TEN, TransactionType.SPEND));
    }
}
