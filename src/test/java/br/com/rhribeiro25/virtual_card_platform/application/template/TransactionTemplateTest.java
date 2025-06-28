
package br.com.rhribeiro25.virtual_card_platform.application.template;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.domain.service.*;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TransactionTemplateTest {

    private SpendTransactionProcessor spendTransactionProcessor;
    private TopUpTransactionProcessor topUpTransactionProcessor;

    @BeforeEach
    void setup() {
        List<TransactionValidation> validations = List.of(
                new TransactionStatusValidationImpl(),
                new TransactionBalanceValidationImpl()
        );
        spendTransactionProcessor = new SpendTransactionProcessor(validations);
        topUpTransactionProcessor = new TopUpTransactionProcessor(validations);
    }

    @Test
    @DisplayName("Should process SPEND transaction successfully")
    void shouldProcessSpendTransactionSuccessfully() {

        CardRepository cardRepository = mock(CardRepository.class);
        TransactionUsecase transactionUsecase = mock(TransactionUsecase.class);

        Card card = new Card.Builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(100))
                .build();

        Transaction transaction = new Transaction.Builder()
                .card(card)
                .amount(BigDecimal.TEN)
                .build();

        when(cardRepository.save(any())).thenReturn(card);
        when(transactionUsecase.create(any())).thenReturn(transaction);

        ReflectionTestUtils.setField(spendTransactionProcessor, "cardRepository", cardRepository);
        ReflectionTestUtils.setField(spendTransactionProcessor, "transactionUsecase", transactionUsecase);

        Card cardResult = spendTransactionProcessor.process(transaction);

        assertNotNull(cardResult);
        assertEquals(new BigDecimal("90"), cardResult.getBalance());
        verify(cardRepository, times(1)).save(any());
        verify(transactionUsecase, times(1)).create(any());
    }

    @Test
    @DisplayName("Should process TOPUP transaction successfully")
    void shouldProcessTopupTransactionSuccessfully() {

        CardRepository cardRepository = mock(CardRepository.class);
        TransactionUsecase transactionUsecase = mock(TransactionUsecase.class);

        Card card = new Card.Builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(100))
                .build();

        Transaction transaction = new Transaction.Builder()
                .card(card)
                .amount(BigDecimal.TEN)
                .build();

        when(cardRepository.save(any())).thenReturn(card);
        when(transactionUsecase.create(any())).thenReturn(transaction);

        ReflectionTestUtils.setField(topUpTransactionProcessor, "cardRepository", cardRepository);
        ReflectionTestUtils.setField(topUpTransactionProcessor, "transactionUsecase", transactionUsecase);

        Card cardResult = topUpTransactionProcessor.process(transaction);

        assertNotNull(cardResult);
        assertEquals(new BigDecimal("110"), cardResult.getBalance());
        verify(cardRepository, times(1)).save(any());
        verify(transactionUsecase, times(1)).create(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when OptimisticLockingFailureException occurs")
    void shouldThrowConflictExceptionOnOptimisticLockingFailureException() {

        CardRepository cardRepository = mock(CardRepository.class);
        TransactionUsecase transactionUsecase = mock(TransactionUsecase.class);

        ReflectionTestUtils.setField(spendTransactionProcessor, "cardRepository", cardRepository);
        ReflectionTestUtils.setField(spendTransactionProcessor, "transactionUsecase", transactionUsecase);

        Card card = new Card.Builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(100))
                .build();

        doThrow(new OptimisticLockingFailureException("optimistic lock")).when(cardRepository).save(any());

        try (MockedStatic<MessageUtils> mocked = mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.getMessage("card.conflict"))
                    .thenReturn("Card already updated by another transaction");

            Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.TEN).build();

            ConflictException exception = assertThrows(ConflictException.class, () -> spendTransactionProcessor.process(transaction));
            assertEquals("Card already updated by another transaction", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should return SPEND as the transaction type")
    void shouldGetTypeReturnSpend() {
        assertEquals(TransactionType.SPEND, spendTransactionProcessor.getType());
    }

    @Test
    @DisplayName("Should return TOPUP as the transaction type")
    void shouldGetTypeReturnTopup() {
        assertEquals(TransactionType.TOPUP, topUpTransactionProcessor.getType());
    }

}
