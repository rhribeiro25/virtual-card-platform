package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportMongoTemplate;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.PersistenceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus.*;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Slf4j
@Component
@StepScope
public class CardProviderWriter extends AbstractBatchWriter<CardProvider, String> {

    private final CardProviderUsecase cardProviderUsecase;

    public CardProviderWriter(BatchAuditImportMongoTemplate batchAuditImportMongoTemplate, PersistenceUtils persistenceUtils, CardProviderUsecase cardProviderUsecase) {
        super(batchAuditImportMongoTemplate, persistenceUtils);
        this.cardProviderUsecase = cardProviderUsecase;
    }


    @Override
    protected String getWriterName() {
        return getClassName(this.getClass());
    }

    @Override
    protected String getField() {
        return BatchAuditImport.Fields.cardProviderId;
    }

    @Override
    protected Optional<CardProvider> findExistingEntityByKey(String key) {
        return Optional.empty();
    }

    @Override
    protected void mergeEntities(CardProvider existing, CardProvider incoming) {

    }

    @Override
    protected void setAuditStatus(BatchAuditImport item, Boolean exists) {
        if (exists == null) item.setStatus(CARD_PROVIDER_NO_ACTION);
        else if (exists) item.setStatus(CARD_PROVIDER_UPDATED);
        else item.setStatus(CARD_PROVIDER_PERSISTED);
    }

    @Override
    protected CardProvider extractEntity(BatchAuditImport item) {
        return item.getCardProvider();
    }

    @Override
    protected String extractKey(CardProvider entity) {
        return entity.getKey();
    }

    @Override
    protected void save(CardProvider entity) {
        cardProviderUsecase.saveByBatch(entity);
    }

}
