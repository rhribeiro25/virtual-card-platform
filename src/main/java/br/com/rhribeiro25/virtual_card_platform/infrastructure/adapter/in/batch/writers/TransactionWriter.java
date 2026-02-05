package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportMongoTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Slf4j
@Component
@StepScope
public class TransactionWriter extends AbstractBatchWriter<Transaction, String> {

    private final TransactionUsecase transactionUsecase;

    public TransactionWriter(TransactionUsecase transactionUsecase,
                             BatchAuditImportMongoTemplate batchAuditMongoTemplate) {
        super(batchAuditMongoTemplate);
        this.transactionUsecase = transactionUsecase;
    }

    @Override
    protected String getWriterName() {
        return getClassName(this.getClass());
    }

    @Override
    protected String getField() {
        return BatchAuditImport.Fields.updatedAt;
    }

    @Override
    protected Optional<Transaction> findExistingEntityByKey(String key) {
        return transactionUsecase.findByRequestId(key);
    }

    @Override
    protected void mergeEntities(Transaction existing, Transaction incoming) {
        transactionUsecase.merge(existing, incoming);
    }

    @Override
    protected Transaction extractEntity(BatchAuditImport item) {
        return item.getTransaction();
    }

    @Override
    protected String extractKey(Transaction entity) {
        return entity.getRequestId();
    }

    @Override
    protected void save(Transaction entity) {
        transactionUsecase.saveByBatch(entity);
    }

}
