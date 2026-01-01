package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.TransactionRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImportProcessedStep;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpTransactionWriter implements ItemWriter<Transaction> {

    private final TransactionRepository transactionRepository;
    private final BatchCacheUtils batchCacheUtils;
    private final JdbcTemplate jdbcTemplate;

    @Value("#{stepExecution.stepName}")
    private String step;

    @Override
    public void write(Chunk<? extends Transaction> chunk) throws Exception {

        // Remove duplicates and check if exists inside cache
        Set<String> keyFilter = new HashSet<>();
        var uniqueList = chunk.getItems().stream()
                .filter(item -> !batchCacheUtils.transactionCache().containsKey(item.getCardExternalId()))
                .filter(item -> keyFilter.add(item.getCardExternalId()))
                .collect(Collectors.toList());

        // Persist all entities
        var persisted = transactionRepository.saveAll(uniqueList);

        // Refresh cache with persisted entities to ensure consistency
        persisted.forEach(item -> batchCacheUtils.transactionCache().put(item.getRequestId().toString(), item));

        // Updating audit step
        List<String> auditIds = new ArrayList<>(batchCacheUtils.auditCache().keySet());
        jdbcTemplate.batchUpdate(
                "UPDATE audit_import SET processed_step = ?, is_processed = ? WHERE id = ?",
                auditIds,
                auditIds.size(),
                (ps, id) -> {
                    ps.setString(1, AuditImportProcessedStep.fromStepName(step).getNextStep());
                    ps.setString(2, "true");
                    ps.setString(3, id);
                }
        );
        batchCacheUtils.auditCache().clear();
    }
}
