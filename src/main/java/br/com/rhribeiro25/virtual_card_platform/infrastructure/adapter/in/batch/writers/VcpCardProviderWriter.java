package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpringBatchWriter.CARD_PROVIDER)
@StepScope
@RequiredArgsConstructor
public class VcpCardProviderWriter implements ItemWriter<BatchAuditImport> {

    private final CardProviderUsecase cardProviderUsecase;
    private final BatchAuditMongoTemplate batchAuditMongoTemplate;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        log.info("Starting: {}", SpringBatchWriter.CARD_PROVIDER);
        for (BatchAuditImport item : chunk.getItems()) {
            CardProvider cardProvider = item.getCardProvider();
            item.setIsTransientEntitySaved(true);
            if (cardProvider != null) {
                try {
                    cardProviderUsecase.saveByBatch(cardProvider);
                } catch (DataIntegrityViolationException e) {
                    item.setIsTransientEntitySaved(false);
                    log.warn("Card Provider already exists: {}", item.getId());
                }
            }
            batchAuditMongoTemplate.updateProcessedFlag(item, BatchAuditImport.Fields.isProcessedCardProvider);
        }
    }

}
