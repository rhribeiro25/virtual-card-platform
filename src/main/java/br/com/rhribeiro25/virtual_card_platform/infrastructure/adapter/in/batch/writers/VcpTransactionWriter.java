package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpringBatchWriter.TRANSACTION)
@StepScope
@SuperBuilder
public class VcpTransactionWriter extends VcpAbstractBatchWriter<Transaction, String> {

    private final TransactionUsecase transactionUsecase;

    @Override
    protected String writerName() {
        return SpringBatchWriter.TRANSACTION;
    }

    @Override
    protected Transaction extractEntity(BatchAuditImport item) {
        return item.getTransaction();
    }

    @Override
    protected String extractKey(Transaction transaction) {
        return transaction.getRequestIdString();
    }

    @Override
    protected void save(Transaction transaction) {
        transactionUsecase.saveByBatch(transaction);
    }

    @Override
    protected String processedFlag() {
        return BatchAuditImport.Fields.isProcessedTransaction;
    }

}
