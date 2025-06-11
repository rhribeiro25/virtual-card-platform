
package br.com.rhribeiro25.virtual_card_platform.infrastructure.repository;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class CardRepositoryIntegrationTest {

    @Autowired
    private CardRepository cardRepository;

    @Test
    @DisplayName("Should save and retrieve a card by ID")
    void shouldSaveAndFindCardById() {
        Card card = new Card.Builder()
                .cardholderName("Integration Test User")
                .balance(BigDecimal.valueOf(200))
                .build();

        Card savedCard = cardRepository.save(card);
        Optional<Card> retrievedCard = cardRepository.findById(savedCard.getId());

        assertTrue(retrievedCard.isPresent());
        assertEquals("Integration Test User", retrievedCard.get().getCardholderName());
        assertEquals(BigDecimal.valueOf(200), retrievedCard.get().getBalance());
    }

    @Test
    @DisplayName("Should return empty when card ID does not exist")
    void shouldReturnEmptyWhenCardNotFound() {
        UUID randomId = UUID.randomUUID();
        Optional<Card> card = cardRepository.findById(randomId);
        assertTrue(card.isEmpty());
    }

    @Test
    @DisplayName("Should delete card successfully")
    void shouldDeleteCard() {
        Card card = new Card.Builder()
                .cardholderName("Delete Test")
                .balance(BigDecimal.valueOf(100))
                .build();

        Card saved = cardRepository.save(card);
        UUID id = saved.getId();

        cardRepository.delete(saved);

        Optional<Card> result = cardRepository.findById(id);
        assertTrue(result.isEmpty());
    }
}
