package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class BatchAuditImportMongoTemplate {

    private final MongoTemplate mongoTemplate;

    public void update(ObjectId auditImportId, UUID entityId, String fieldName, BatchAuditImportStatus status) {
        Update update = new Update()
                .set(fieldName, entityId)
                .set("status", status);

        mongoTemplate.updateMulti(
                Query.query(Criteria.where("_id").is(auditImportId)),
                update,
                BatchAuditImport.class
        );
    }


}

