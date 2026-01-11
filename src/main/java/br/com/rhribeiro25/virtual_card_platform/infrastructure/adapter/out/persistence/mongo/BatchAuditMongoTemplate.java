package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@AllArgsConstructor
public class BatchAuditMongoTemplate {

    private final MongoTemplate mongoTemplate;

    public void updatePersistedEntityId(ObjectId auditImportId, UUID entityId, String fieldName) {
        mongoTemplate.updateMulti(
                query(Criteria.where("_id").is(auditImportId)),
                Update.update(fieldName, entityId),
                BatchAuditImport.class
        );
    }

}

