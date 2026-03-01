package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface BatchAuditImportRepository extends MongoRepository<BatchAuditImport, UUID> {

    Optional<BatchAuditImport> findTopByOrderByActionFileDateDesc();
}
