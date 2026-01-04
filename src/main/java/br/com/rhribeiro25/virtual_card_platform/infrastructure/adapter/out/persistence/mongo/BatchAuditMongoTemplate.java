package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@AllArgsConstructor
public class BatchAuditMongoTemplate {

    private final MongoTemplate mongoTemplate;

    public void updateProcessedFlag(BatchAuditImport auditImport, String fieldName) {
        mongoTemplate.updateMulti(
                query(Criteria.where("_id").is(auditImport.getId())),
                Update.update(fieldName, auditImport.getAuxFlag()),
                BatchAuditImport.class
        );
    }

}

