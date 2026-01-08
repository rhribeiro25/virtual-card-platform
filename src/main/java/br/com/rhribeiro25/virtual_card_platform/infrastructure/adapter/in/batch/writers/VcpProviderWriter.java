package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component(SpringBatchWriter.PROVIDER)
@StepScope
public class VcpProviderWriter extends VcpAbstractBatchWriter<Provider, String> {

    private final ProviderUsecase providerUsecase;

    public VcpProviderWriter(ProviderUsecase providerUsecase,
                             BatchAuditMongoTemplate batchAuditMongoTemplate) {
        super(batchAuditMongoTemplate);
        this.providerUsecase = providerUsecase;
    }

    @Override
    protected String writerName() {
        return SpringBatchWriter.PROVIDER;
    }

    @Override
    protected Provider extractEntity(BatchAuditImport item) {
        return item.getProvider();
    }

    @Override
    protected String extractKey(Provider provider) {
        return provider.getCode();
    }

    @Override
    protected void save(Provider provider) {
        providerUsecase.saveByBatch(provider);
    }

    @Override
    protected String getField() {
        return BatchAuditImport.Fields.persistedProviderId;
    }

    @Override
    protected Optional<UUID> findExistingEntityIdByKey(String code) {
        return providerUsecase.findIdByCode(code);
    }

}
