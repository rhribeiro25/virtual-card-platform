package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.TransactionRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VcpTransactionWriter implements ItemWriter<Transaction> {

    private final TransactionRepository transactionRepository;
    private final BatchCacheUtils batchCacheUtils;

    @Override
    public void write(Chunk<? extends Transaction> chunk) throws Exception {
        // Get all items from the current chunk
        List<? extends Transaction> transactions = chunk.getItems();

        // Associate each transaction with its corresponding Card from the cache
        transactions.forEach(tx -> tx.setCard(batchCacheUtils.cardCache().get(tx.getCardExternalId())));

        // Remove duplicates within the current chunk
        List<Transaction> uniqueTransactions = transactions.stream()
                .distinct()
                .collect(Collectors.toList());

        // Persist all unique transactions to the database
        List<Transaction> persistedTransactions = transactionRepository.saveAll(uniqueTransactions);

        // Update the transaction cache with the persisted transactions
        persistedTransactions.forEach(tx ->
                batchCacheUtils.transactionCache().put(tx.getRequestId().toString(), tx)
        );
    }
}
