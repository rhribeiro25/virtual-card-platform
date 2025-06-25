
package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.application.template.SpendTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.application.template.TopUpTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

        MessageUtil.setMessageSource(messageSource);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("mocked-message");
    }

    @Test
    @DisplayName("Should create card successfully")
    void createCardSuccessfully() {
        when(cardRepository.save(any())).thenReturn(card);
        Card result = cardUsecase.create(card);
        assertEquals("Renan", result.getCardholderName());
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Should throw ConflictException when repository fails")
    void createCardShouldThrowOptimisticLockException() {
        when(cardRepository.save(any())).thenThrow(new RuntimeException());
        assertThrows(ConflictException.class, () -> cardUsecase.create(card));
    }

    @Test
    @DisplayName("Should process spend successfully")
    void spendShouldSucceed() {
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(spendProcessor.process(any(), any())).thenReturn(card);

        Card result = cardUsecase.spend(cardId, BigDecimal.TEN);
        assertEquals(card, result);
    }

    @Test
    @DisplayName("Should fail spend when card not found")
    void spendShouldFailWhenCardNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardUsecase.spend(cardId, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Should process top up successfully")
    void topUpShouldSucceed() {
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(topUpProcessor.process(any(), any())).thenReturn(card);

        Card result = cardUsecase.topUp(cardId, BigDecimal.TEN);
        assertEquals(card, result);
    }

    @Test
    @DisplayName("Should fail top up when card not found")
    void topUpShouldFailWhenCardNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardUsecase.topUp(cardId, BigDecimal.TEN));
    }

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

    @Test
    @DisplayName("Should return transactions for valid card")
    void getTransactionsByValidCardId() {
        Page<Transaction> transactions = new PageImpl<>(Collections.emptyList());
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(transactionUsecase.getTransactionsByCardId(any(), any(Pageable.class))).thenReturn(transactions);

        Page<Transaction> result = cardUsecase.getTransactionsByValidCardId(cardId, Pageable.unpaged());
        assertNotNull(result);
    }
}
