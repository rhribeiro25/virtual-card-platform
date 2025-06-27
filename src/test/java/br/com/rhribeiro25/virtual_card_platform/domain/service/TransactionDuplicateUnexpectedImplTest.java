package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.CacheUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionDuplicateUnexpectedImplTest {

    private TransactionUsecase transactionUsecase;
    private TransactionDuplicateUnexpectedImpl validator;

    private final UUID cardId = UUID.randomUUID();
    private final UUID requestId = UUID.randomUUID();
    private Card card;

    @BeforeEach
    void setUp() {
        transactionUsecase = mock(TransactionUsecase.class);
        validator = new TransactionDuplicateUnexpectedImpl(transactionUsecase);

        card = new Card.Builder().id(cardId).cardholderName("Test User").build();
    }

    @Test
    @DisplayName("Should skip validation when requestId is null")
    void shouldSkipValidationWhenRequestIdIsNull() {
        Transaction transaction = new Transaction.Builder().card(card).requestId(null).build();
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    @DisplayName("Should throw ConflictException when transaction exists in cache")
    void shouldThrowConflictWhenTransactionExistsInCache() {
        Transaction transaction = new Transaction.Builder().card(card).requestId(requestId).build();

        Transaction duplicate = new Transaction.Builder().card(card).requestId(requestId).build();
        Page<Transaction> page = new PageImpl<>(Collections.singletonList(duplicate));

        try (MockedStatic<CacheUtils> cacheMock = mockStatic(CacheUtils.class);
             MockedStatic<MessageUtils> msgMock = mockStatic(MessageUtils.class)) {

            cacheMock.when(() -> CacheUtils.getFromCache("transactionsByCardId", cardId, Page.class))
                    .thenReturn(page);

            msgMock.when(() -> MessageUtils.getMessage("card.duplicateTransaction"))
                    .thenReturn("Duplicated transaction");

            ConflictException ex = assertThrows(ConflictException.class, () -> validator.validate(transaction));
            assertEquals("Duplicated transaction", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Should throw ConflictException when transaction exists in database")
    void shouldThrowConflictWhenTransactionExistsInDatabase() {
        Transaction transaction = new Transaction.Builder().card(card).requestId(requestId).build();

        try (MockedStatic<CacheUtils> cacheMock = mockStatic(CacheUtils.class);
             MockedStatic<MessageUtils> msgMock = mockStatic(MessageUtils.class)) {

            cacheMock.when(() -> CacheUtils.getFromCache("transactionsByCardId", cardId, Page.class))
                    .thenReturn(null);

            when(transactionUsecase.verifyDuplicateTransaction(cardId, requestId))
                    .thenReturn(Optional.of(transaction));

            msgMock.when(() -> MessageUtils.getMessage("card.duplicateTransaction"))
                    .thenReturn("Duplicated transaction");

            ConflictException ex = assertThrows(ConflictException.class, () -> validator.validate(transaction));
            assertEquals("Duplicated transaction", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Should pass validation when no duplicate exists")
    void shouldPassWhenNoDuplicateExists() {
        Transaction transaction = new Transaction.Builder().card(card).requestId(requestId).build();

        try (MockedStatic<CacheUtils> cacheMock = mockStatic(CacheUtils.class)) {
            cacheMock.when(() -> CacheUtils.getFromCache("transactionsByCardId", cardId, Page.class))
                    .thenReturn(null);

            when(transactionUsecase.verifyDuplicateTransaction(cardId, requestId))
                    .thenReturn(Optional.empty());

            assertDoesNotThrow(() -> validator.validate(transaction));
        }
    }

    @Test
    @DisplayName("Should support only SPEND and TOPUP transaction types")
    void shouldSupportSpecificTransactionTypes() {
        assertTrue(validator.supports(TransactionType.SPEND));
        assertTrue(validator.supports(TransactionType.TOPUP));
    }
}
