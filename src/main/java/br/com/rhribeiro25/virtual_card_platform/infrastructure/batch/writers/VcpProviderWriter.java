package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.ProviderRepository;
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
public class VcpProviderWriter implements ItemWriter<Provider> {

    private final ProviderRepository providerRepository;
    private final BatchCacheUtils batchCacheUtils;

    @Override
    public void write(Chunk<? extends Provider> chunk) throws Exception {
        // Convert to a mutable list
        List<? extends Provider> providers = chunk.getItems();

        // Remove duplicates based on equals/hashCode
        List<Provider> uniqueProviders = providers.stream()
                .distinct()
                .collect(Collectors.toList());

        // Update cache with incoming providers
        uniqueProviders.forEach(provider -> batchCacheUtils.providerCache().put(provider.getCode(), provider));

        // Persist all providers
        List<Provider> persistedProviders = providerRepository.saveAll(new ArrayList<>(batchCacheUtils.providerCache().values()));

        // Refresh cache with persisted providers to ensure consistency
        persistedProviders.forEach(provider -> batchCacheUtils.providerCache().put(provider.getCode(), provider));
    }
}
