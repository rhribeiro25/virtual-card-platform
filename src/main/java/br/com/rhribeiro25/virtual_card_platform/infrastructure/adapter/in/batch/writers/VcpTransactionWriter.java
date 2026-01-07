package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component(SpringBatchWriter.TRANSACTION)
@StepScope
@RequiredArgsConstructor
public class VcpTransactionWriter implements ItemWriter<BatchAuditImport> {

    private final TransactionUsecase transactionUsecase;
    private final BatchAuditMongoTemplate batchAuditMongoTemplate;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        log.info("Starting: {}", SpringBatchWriter.TRANSACTION);
        List<UUID> chunkCheck = new ArrayList<>();
        for (BatchAuditImport item : chunk.getItems()) {
            Transaction transaction = item.getTransaction();
            item.setIsTransientEntitySaved(true);
            if (transaction != null && !chunkCheck.contains(transaction.getRequestId())) {
                try {
                    chunkCheck.add(transaction.getRequestId());
                    transactionUsecase.saveByBatch(transaction);
                } catch (DataIntegrityViolationException e) {
                    item.setIsTransientEntitySaved(false);
                    log.warn("Transaction already exists: {}", item.getId());
                }
            }
            batchAuditMongoTemplate.updateProcessedFlag(item, BatchAuditImport.Fields.isProcessedTransaction);
        }
    }


}
