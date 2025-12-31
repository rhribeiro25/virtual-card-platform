package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writer;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.CardProviderRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.ProviderRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.TransactionRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config.BatchCacheConfig;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto.VcpModelsGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
public class VcpItemWriter {

    private final CardRepository cardRepository;
    private final ProviderRepository providerRepository;
    private final TransactionRepository transactionRepository;
    private final CardProviderRepository cardProviderRepository;
    private final BatchCacheConfig batchCacheConfig;

    @Bean
    public ItemWriter<VcpModelsGroup> write() {
        return itemWriter -> {

            // Persisting Providers
            itemWriter.getItems().stream()
                    .map(VcpModelsGroup::provider)
                    .distinct()
                    .forEach(provider -> batchCacheConfig.providerCache().put(provider.getCode(), provider));
            var persistedProviders = providerRepository.saveAll(new ArrayList<>(batchCacheConfig.providerCache().values()));
            persistedProviders.forEach(provider -> batchCacheConfig.providerCache().put(provider.getCode(), provider));

            // Persisting Cards
            itemWriter.getItems().stream()
                    .map(VcpModelsGroup::card)
                    .distinct()
                    .forEach(card -> batchCacheConfig.cardCache().put(card.getExternalId(), card));
            var persistedCards = cardRepository.saveAll(new ArrayList<>(batchCacheConfig.cardCache().values()));
            persistedCards.forEach(card -> batchCacheConfig.cardCache().put(card.getExternalId(), card));

            // Persisting Transactions
            itemWriter.forEach(item -> item.transaction().setCard(batchCacheConfig.cardCache().get(item.card().getExternalId())));
            itemWriter.getItems().stream()
                    .map(VcpModelsGroup::transaction)
                    .distinct()
                    .forEach(transaction -> batchCacheConfig.transactionCache().put(transaction.getRequestId().toString(), transaction));
            var persistedTransactions = transactionRepository.saveAll(new ArrayList<>(batchCacheConfig.transactionCache().values()));
            persistedTransactions.forEach(tx -> batchCacheConfig.transactionCache().put(tx.getRequestId().toString(), tx));

            // Persisting CardProviders
            itemWriter.forEach(item -> {
                item.cardProvider().setCard(batchCacheConfig.cardCache().get(item.card().getExternalId()));
                item.cardProvider().setProvider(batchCacheConfig.providerCache().get(item.provider().getCode()));
            });
            cardProviderRepository.saveAll(itemWriter.getItems().stream().map(VcpModelsGroup::cardProvider).toList());

        };
    }

}
