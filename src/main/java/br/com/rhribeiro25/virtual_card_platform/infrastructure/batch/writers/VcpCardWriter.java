package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VcpCardWriter implements ItemWriter<Card> {

    private final CardRepository cardRepository;
    private final BatchCacheUtils batchCacheUtils;

    @Override
    public void write(Chunk<? extends Card> chunk) throws Exception {
        // Convert to mutable list
        List<? extends Card> cardList = chunk.getItems();

        // Remove duplicates
        List<Card> uniqueCards = cardList.stream()
                .distinct()
                .collect(Collectors.toList());

        // Update cache with incoming cards
        uniqueCards.forEach(card -> batchCacheUtils.cardCache().put(card.getExternalId(), card));

        // Persist all cards
        List<Card> persistedCards = cardRepository.saveAll(new ArrayList<>(batchCacheUtils.cardCache().values()));

        // Refresh cache with persisted cards to ensure consistency
        persistedCards.forEach(card -> batchCacheUtils.cardCache().put(card.getExternalId(), card));
    }
}
