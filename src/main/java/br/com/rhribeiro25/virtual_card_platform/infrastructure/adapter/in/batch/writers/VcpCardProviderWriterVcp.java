package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpringBatchWriter.CARD_PROVIDER)
@StepScope
public class VcpCardProviderWriterVcp extends VcpAbstractBatchWriter<CardProvider, String> {

    private final CardProviderUsecase cardProviderUsecase;

    public VcpCardProviderWriterVcp(CardProviderUsecase cardProviderUsecase,
                                    BatchAuditMongoTemplate batchAuditMongoTemplate) {
        super(batchAuditMongoTemplate);
        this.cardProviderUsecase = cardProviderUsecase;
    }

    @Override
    protected String writerName() {
        return SpringBatchWriter.CARD_PROVIDER;
    }

    @Override
    protected CardProvider extractEntity(BatchAuditImport item) {
        return item.getCardProvider();
    }

    @Override
    protected String extractKey(CardProvider cardProvider) {
        return cardProvider.getKey();
    }

    @Override
    protected void save(CardProvider cardProvider) {
        cardProviderUsecase.saveByBatch(cardProvider);
    }

    @Override
    protected String processedFlag() {
        return BatchAuditImport.Fields.isProcessedCardProvider;
    }

}
