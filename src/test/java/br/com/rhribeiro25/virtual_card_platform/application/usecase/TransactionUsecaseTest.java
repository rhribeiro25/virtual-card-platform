
package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.domain.service.TransactionDuplicateUnexpectedImpl;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.TransactionRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.CacheUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class TransactionUsecaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private TransactionUsecase transactionUsecase;

    private TransactionDuplicateUnexpectedImpl validation;
    private Card card;
    private UUID cardId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        cardId = UUID.randomUUID();
        card = new Card.Builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(100))
                .build();

        MessageUtils.setMessageSource(messageSource);
        when(messageSource.getMessage(eq("card.transactionRangeMinutes"), any(), any())).thenReturn("10");
        when(messageSource.getMessage(eq("card.duplicateTransaction"), any(), any())).thenReturn("Duplicate transaction");
        when(messageSource.getMessage(eq("card.spend.recent.minutes"), any(), any())).thenReturn("15");
        validation = new TransactionDuplicateUnexpectedImpl(transactionUsecase);
    }

    @Test
    @DisplayName("Should create a transaction successfully")
    void createTransactionSuccessfully() {
        Transaction tx = new Transaction();
        when(transactionRepository.save(any())).thenReturn(tx);

        Transaction result = transactionUsecase.create(tx);
        assertNotNull(result);
        verify(transactionRepository).save(tx);
    }

    @Test
    @DisplayName("Should count recent spend transactions")
    void shouldCountRecentSpends() {
        when(transactionRepository.countRecentTransactions(any(), any(), any()))
                .thenReturn(3L);

        long count = transactionUsecase.countRecentSpends(cardId, TransactionType.SPEND);
        assertEquals(3L, count);
    }

    @Test
    @DisplayName("Should return paged transactions by cardId")
    void shouldReturnTransactionsByCardId() {
        Page<Transaction> page = new PageImpl<>(Collections.emptyList());
        when(transactionRepository.findByCardId(eq(cardId), any())).thenReturn(page);

        Page<Transaction> result = transactionUsecase.getTransactionsByCardId(cardId, Pageable.unpaged());
        assertNotNull(result);
        verify(transactionRepository).findByCardId(eq(cardId), any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when duplicate transaction exists in time range")
    void shouldThrowExceptionWhenDuplicateTransactionExists() {
        BigDecimal amount = BigDecimal.TEN;
        TransactionType type = TransactionType.SPEND;

        when(messageSource.getMessage(eq("card.conflict"), any(), any())).thenReturn("10");

        when(transactionRepository.findDuplicateBetweenRangeTimeTransaction(
                eq(amount),
                eq(card.getId()),
                any(),
                any(),
                eq(type)
        )).thenReturn(Optional.of(new Transaction()));

        assertThrows(BadRequestException.class,
                () -> transactionUsecase.isDuplicateTransaction(card, amount, type));
    }

    @Test
    @DisplayName("Should NOT throw exception when existingTransaction is empty")
    void shouldNotThrowWhenNoExistingTransaction() {
        UUID requestId = UUID.randomUUID();
        Transaction transaction = new Transaction.Builder()
                .card(card)
                .requestId(requestId)
                .build();

        try (MockedStatic<CacheUtils> cacheMock = Mockito.mockStatic(CacheUtils.class)) {
            cacheMock.when(() -> CacheUtils.getFromCache("transactionsByCardId", cardId, Page.class))
                    .thenReturn(null);

            when(transactionUsecase.verifyDuplicateTransaction(cardId, requestId))
                    .thenReturn(Optional.empty());

            assertDoesNotThrow(() -> validation.validate(transaction));
        }
    }

}
