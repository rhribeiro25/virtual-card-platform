//package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;
//
//import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
//import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
//import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
//import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
//import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
//import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
//import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@Slf4j
//@Component(SpringBatchWriter.TRANSACTION)
//@StepScope
//public class VcpTransactionWriter extends VcpAbstractBatchWriter<Transaction> {
//
//    private final TransactionUsecase transactionUsecase;
//
//    public VcpTransactionWriter(TransactionUsecase transactionUsecase,
//                                BatchAuditMongoTemplate batchAuditMongoTemplate) {
//        super(batchAuditMongoTemplate);
//        this.transactionUsecase = transactionUsecase;
//    }
//
//    @Override
//    protected String getWriterName() {
//        return SpringBatchWriter.TRANSACTION;
//    }
//
//    @Override
//    protected String getField() {
//        return BatchAuditImport.Fields.persistedTransactionId;
//    }
//
//    @Override
//    protected Transaction extractEntity(BatchAuditImport item) {
//        return item.getTransaction();
//    }
//
//    @Override
//    protected UUID saving(Transaction entity, ActionType actionType) {
//        return transactionUsecase.upsert(entity, actionType);
//    }
//
//}
