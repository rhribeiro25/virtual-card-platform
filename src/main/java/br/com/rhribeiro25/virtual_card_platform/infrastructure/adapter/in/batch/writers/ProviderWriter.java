package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportMongoTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Slf4j
@Component
@StepScope
public class ProviderWriter extends AbstractBatchWriter<Provider, String> {

    private final ProviderUsecase providerUsecase;

    public ProviderWriter(ProviderUsecase providerUsecase,
                          BatchAuditImportMongoTemplate batchAuditImportMongoTemplate) {
        super(batchAuditImportMongoTemplate);
        this.providerUsecase = providerUsecase;
    }

    @Override
    protected String getWriterName() {
        return getClassName(this.getClass());
    }

    @Override
    protected String getField() {
        return BatchAuditImport.Fields.providerId;
    }

    @Override
    protected Optional<Provider> findExistingEntityByKey(String key) {
        return Optional.empty();
    }

    @Override
    protected void mergeEntities(Provider existing, Provider incoming) {

    }

    @Override
    protected Provider extractEntity(BatchAuditImport item) {
        return item.getProvider();
    }

    @Override
    protected String extractKey(Provider entity) {
        return entity.getCode();
    }

    @Override
    protected void save(Provider entity) {
        providerUsecase.saveByBatch(entity);
    }


}
