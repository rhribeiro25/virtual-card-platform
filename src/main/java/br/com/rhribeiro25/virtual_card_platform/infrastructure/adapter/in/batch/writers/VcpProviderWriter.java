package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpringBatchWriter.PROVIDER)
@StepScope
@SuperBuilder
public class VcpProviderWriter extends VcpAbstractBatchWriter<Provider, String> {

    private final ProviderUsecase providerUsecase;

    @Override
    protected String writerName() {
        return SpringBatchWriter.CARD;
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
    protected String processedFlag() {
        return BatchAuditImport.Fields.isProcessedProvider;
    }
}
