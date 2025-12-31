package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.CardProviderRepository;
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
public class VcpCardProviderWriter implements ItemWriter<CardProvider> {

    private final CardProviderRepository cardProviderRepository;
    private final BatchCacheUtils batchCacheUtils;

    @Override
    public void write(Chunk<? extends CardProvider> chunk) throws Exception {
        // Convert to mutable list
        List<? extends CardProvider> cardProviderList = chunk.getItems();

        // Remove duplicates
        List<CardProvider> uniqueCardProviders = cardProviderList.stream()
                .distinct()
                .collect(Collectors.toList());

        // Set references from cache and update local cache
        uniqueCardProviders.forEach(cp -> {
            String[] codes = cp.getKeyMap().split("_");
            cp.setCard(batchCacheUtils.cardCache().get(codes[0]));
            cp.setProvider(batchCacheUtils.providerCache().get(codes[1]));
            batchCacheUtils.cardProviderCache().put(cp.getKeyMap(), cp);
        });

        // Persist all CardProviders
        List<CardProvider> persisted = cardProviderRepository.saveAll(
                new ArrayList<>(batchCacheUtils.cardProviderCache().values())
        );

        // Refresh cache with persisted entries to maintain consistency
        persisted.forEach(cp -> batchCacheUtils.cardProviderCache().put(cp.getKeyMap(), cp));
    }
}
