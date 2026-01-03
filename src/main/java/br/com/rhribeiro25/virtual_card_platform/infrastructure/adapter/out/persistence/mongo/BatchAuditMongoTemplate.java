package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchStep;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@AllArgsConstructor
public class BatchAuditMongoTemplate {

    private final MongoTemplate mongoTemplate;


    public MongoPagingItemReader<BatchAuditImport> findAll() {
        MongoPagingItemReader<BatchAuditImport> reader = new MongoPagingItemReader<>();

        reader.setTemplate(mongoTemplate);
        reader.setTargetType(BatchAuditImport.class);
        reader.setPageSize(1000);
        reader.setSort(Map.of("_id", Sort.Direction.ASC));
        reader.setQuery("{}");
        return reader;
    }

    public void updateProcessedFlag(BatchAuditImport auditImport, String fieldName) {
        mongoTemplate.updateMulti(
                query(Criteria.where("_id").is(auditImport.getId())),
                Update.update(fieldName, auditImport.getAuxFlag()),
                BatchAuditImport.class
        );
    }

    private String getQueryByStep(String step) {
        return switch (step) {
            case SpringBatchStep.CARD -> "{ isProcessedCard: false }";
            case SpringBatchStep.PROVIDER -> "{ isProcessedProvider: false }";
            case SpringBatchStep.CARD_PROVIDER -> "{ isProcessedCardProvider: false }";
            case SpringBatchStep.TRANSACTION -> "{ isProcessedTransaction: false }";
            default -> throw new IllegalStateException("Invalid step: " + step);
        };
    }

}

