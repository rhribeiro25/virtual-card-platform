package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.service.ProviderService;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component(SpringBatchProcessor.PROVIDER)
@StepScope
@RequiredArgsConstructor
public class VcpProviderProcessor extends VcpAbstractBatchProcessor<Provider> {

    private final ProviderUsecase providerUsecase;
    private final ProviderService providerService;

    @Override
    protected boolean dependenciesResolved(BatchAuditImport item) {
        return true;
    }

    @Override
    protected Provider buildEntity(CsvFileRow row, BatchAuditImport item) {
        return Provider.builder()
                .code(row.getProviderCode())
                .createdAt(LocalDateTime.now())
                .status(providerService.mapStatus(row.getProviderState()))
                .country(row.getProviderCountry())
                .build();
    }

    @Override
    protected void attachEntity(BatchAuditImport item, Provider provider) {
        item.setProvider(provider);
    }

}