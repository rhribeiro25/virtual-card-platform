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
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionDuplicateUnexpectedImplTest {

    private TransactionUsecase transactionUsecase;
    private TransactionDuplicateUnexpectedImpl validation;

    private final UUID cardId = UUID.randomUUID();
    private final UUID requestId = UUID.randomUUID();
    private Card card;

    @BeforeEach
    void setUp() {

        transactionUsecase = mock(TransactionUsecase.class);
        validation = new TransactionDuplicateUnexpectedImpl(transactionUsecase);

        card = new Card.Builder().id(cardId).cardholderName("Test User").build();
    }

    @Test
    @DisplayName("Should skip validation when requestId is null")
    void shouldSkipValidationWhenRequestIdIsNull() {
        Transaction transaction = new Transaction.Builder().card(card).requestId(null).build();
        assertDoesNotThrow(() -> validation.validate(transaction));
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

            ConflictException ex = assertThrows(ConflictException.class, () -> validation.validate(transaction));
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

            ConflictException ex = assertThrows(ConflictException.class, () -> validation.validate(transaction));
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

            assertDoesNotThrow(() -> validation.validate(transaction));
        }
    }

    @Test
    @DisplayName("Should not detect duplicate by requestId or cardId when exists data in cache")
    void shouldNotDetectDuplicateWhenIdsDifferInCache() {
        MessageSource messageSourceMock = Mockito.mock(MessageSource.class);
        Mockito.when(messageSourceMock.getMessage(Mockito.anyString(), Mockito.any(), Mockito.any(Locale.class)))
                .thenReturn("Duplicated transaction message");
        MessageUtils.setMessageSource(messageSourceMock);

        Transaction transaction = new Transaction.Builder().card(card).requestId(requestId).build();
        Transaction Transaction2 = new Transaction.Builder().card(card).requestId(UUID.randomUUID()).build();
        List<Transaction> transactions = Collections.singletonList(transaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions);

        try (MockedStatic<CacheUtils> cacheMock = mockStatic(CacheUtils.class)) {
            cacheMock.when(() -> CacheUtils.getFromCache("transactionsByCardId", cardId, Page.class))
                    .thenReturn(transactionPage);
            assertDoesNotThrow(() -> validation.validate(Transaction2));
        }

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
