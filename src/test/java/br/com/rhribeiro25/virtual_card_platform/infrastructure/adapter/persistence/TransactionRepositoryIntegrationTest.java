
package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    private Card card;

    @BeforeEach
    void setup() {
        card = Card.builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(500))
                .active(true)
                .internationalAllowed(true)
                .createdAt(LocalDateTime.now())
                .build();

        card = cardRepository.save(card);
    }

    @Test
    @DisplayName("Should find duplicate transaction")
    void shouldFindDuplicateBetweenRangeTimeTransaction() {
        Transaction tx = Transaction.builder()
                .card(card)
                .amount(BigDecimal.TEN)
                .type(TransactionType.SPEND)
                .createdAt(LocalDateTime.now())
                .requestId(UUID.randomUUID())
                .build();

        transactionRepository.save(tx);

        LocalDateTime start = LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusMinutes(5);

        Optional<Transaction> result = transactionRepository.findDuplicateBetweenRangeTimeTransaction(
                BigDecimal.TEN,
                card.getId(),
                start,
                end,
                TransactionType.SPEND
        );

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Should count recent transactions")
    void shouldCountRecentTransactions() {
        Transaction tx = Transaction.builder()
                .card(card)
                .amount(BigDecimal.valueOf(25))
                .type(TransactionType.TOPUP)
                .createdAt(LocalDateTime.now())
                .requestId(UUID.randomUUID())
                .build();

        transactionRepository.save(tx);

        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        long count = transactionRepository.countRecentTransactions(
                card.getId(),
                TransactionType.TOPUP,
                since
        );

        assertEquals(1, count);
    }

    @Test
    @DisplayName("Should return paginated transactions")
    void shouldReturnPaginatedTransactions() {
        Transaction tx1 = Transaction.builder()
                .card(card)
                .amount(BigDecimal.valueOf(30))
                .type(TransactionType.TOPUP)
                .createdAt(LocalDateTime.now())
                .requestId(UUID.randomUUID())
                .build();

        transactionRepository.save(tx1);

        Page<Transaction> result = transactionRepository.findByCardId(card.getId(), PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty on no duplicate")
    void shouldReturnEmptyOnNoDuplicateTransaction() {
        LocalDateTime start = LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusMinutes(5);

        Optional<Transaction> result = transactionRepository.findDuplicateBetweenRangeTimeTransaction(
                BigDecimal.TEN,
                card.getId(),
                start,
                end,
                TransactionType.SPEND
        );

        assertTrue(result.isEmpty());
    }
}
