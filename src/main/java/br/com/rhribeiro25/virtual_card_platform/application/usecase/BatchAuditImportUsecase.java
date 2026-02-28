package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BatchAuditImportUsecase {

    private final BatchAuditImportRepository batchAuditImportRepository;

    public Optional<BatchAuditImport> findFirstByOrderByActionFileDateDesc() {
        return batchAuditImportRepository.findTopByOrderByActionFileDateDesc();
    }

    public void update(BatchAuditImport item) {
        batchAuditImportRepository.save(item);
    }
}
