
package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.application.template.SpendTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.application.template.TopUpTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.TransactionRequest;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.InternalServerErrorException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
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
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
class CardUsecaseTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionUsecase transactionUsecase;

    @Mock
    private SpendTransactionProcessor spendProcessor;

    @Mock
    private TopUpTransactionProcessor topUpProcessor;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CardUsecase cardUsecase;

    private Card card;
    private UUID cardId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        cardId = UUID.randomUUID();
        card = new Card.Builder()
                .cardholderName("Renan")
                .balance(BigDecimal.valueOf(100))
                .build();

        MessageUtils.setMessageSource(messageSource);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("mocked-message");
    }

    // --- create ---

    @Test
    @DisplayName("Should create card successfully")
    void createCardSuccessfully() {
        when(cardRepository.save(any())).thenReturn(card);
        Card result = cardUsecase.create(card);
        assertEquals("Renan", result.getCardholderName());
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Should throw ConflictException on OptimisticLockingFailureException")
    void createCardConflictOptimisticLock() {
        try (MockedStatic<MessageUtils> mocked = Mockito.mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.getMessage("card.conflict")).thenReturn("Conflict");
            when(cardRepository.save(any())).thenThrow(new OptimisticLockingFailureException("conflict"));
            ConflictException ex = assertThrows(ConflictException.class, () -> cardUsecase.create(card));
            assertEquals("Conflict", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Should throw ConflictException on DataIntegrityViolationException")
    void createCardConflictDataIntegrity() {
        try (MockedStatic<MessageUtils> mocked = Mockito.mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.getMessage("card.conflict")).thenReturn("Conflict");
            when(cardRepository.save(any())).thenThrow(new DataIntegrityViolationException("data error"));
            ConflictException ex = assertThrows(ConflictException.class, () -> cardUsecase.create(card));
            assertEquals("Conflict", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Should throw InternalServerErrorException on other exceptions")
    void createCardInternalError() {
        when(cardRepository.save(any())).thenThrow(new RuntimeException("unknown"));
        assertThrows(InternalServerErrorException.class, () -> cardUsecase.create(card));
    }

    // --- spend ---

    @Test
    @DisplayName("Should process spend successfully")
    void spendShouldSucceed() {
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(spendProcessor.process(any())).thenReturn(card);

        TransactionRequest transaction = new TransactionRequest(BigDecimal.TEN, UUID.randomUUID());

        Card result = cardUsecase.spend(cardId, transaction);

        assertEquals(card, result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when spending with card not found")
    void spendShouldFailWhenCardNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        TransactionRequest transaction = new TransactionRequest(BigDecimal.TEN, UUID.randomUUID());
        assertThrows(NotFoundException.class, () -> cardUsecase.spend(cardId, transaction));
    }

    // --- topUp ---

    @Test
    @DisplayName("Should process top up successfully")
    void topUpShouldSucceed() {
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(topUpProcessor.process(any())).thenReturn(card);

        TransactionRequest transaction = new TransactionRequest(BigDecimal.TEN, UUID.randomUUID());

        Card result = cardUsecase.topUp(cardId, transaction);

        assertEquals(card, result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when top up with card not found")
    void topUpShouldFailWhenCardNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        TransactionRequest transaction = new TransactionRequest(BigDecimal.TEN, UUID.randomUUID());
        assertThrows(NotFoundException.class, () -> cardUsecase.topUp(cardId, transaction));
    }

    // --- getCardById ---

    @Test
    @DisplayName("Should get card by ID successfully")
    void getCardByIdSuccessfully() {
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        Card result = cardUsecase.getCardById(cardId);
        assertEquals(card, result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when card not found by ID")
    void getCardByIdNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardUsecase.getCardById(cardId));
    }

    // --- getTransactionsByValidCardId ---

    @Test
    @DisplayName("Should return transactions for valid card")
    void getTransactionsByValidCardId() {
        Page<Transaction> transactions = new PageImpl<>(Collections.emptyList());
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(transactionUsecase.getTransactionsByCardId(any(), any(Pageable.class))).thenReturn(transactions);

        Page<Transaction> result = cardUsecase.getTransactionsByValidCardId(cardId, Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when getting transactions for non-existent card")
    void getTransactionsByValidCardIdNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardUsecase.getTransactionsByValidCardId(cardId, Pageable.unpaged()));
    }
}
