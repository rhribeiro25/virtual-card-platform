package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchWriter;
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

@Slf4j
@Component(SpringBatchWriter.PROVIDER)
@StepScope
@RequiredArgsConstructor
public class VcpProviderWriter implements ItemWriter<BatchAuditImport> {

    private final ProviderUsecase providerUsecase;
    private final BatchAuditMongoTemplate batchAuditMongoTemplate;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        log.info("Starting: {}", SpringBatchWriter.PROVIDER);
        List<String> chunkCheck = new ArrayList<>();
        for (BatchAuditImport item : chunk.getItems()) {
            Provider provider = item.getProvider();
            item.setIsTransientEntitySaved(true);
            if (provider != null && !chunkCheck.contains(provider.getCode())) {
                try {
                    chunkCheck.add(provider.getCode());
                    providerUsecase.saveByBatch(provider);
                } catch (DataIntegrityViolationException e) {
                    item.setIsTransientEntitySaved(false);
                    log.warn("Provider already exists: {}", item.getId());
                }
            }
            batchAuditMongoTemplate.updateProcessedFlag(item, "isProcessedProvider");
        }
    }
}
