package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.domain.service.ProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ProviderProcessor extends AbstractBatchProcessor<Provider> {

    private final ProviderService providerService;
    private final ProviderUsecase providerUsecase;

    @Override
    protected boolean dependenciesResolved(BatchAuditImport item) {
        return true;
    }

    @Override
    protected Provider buildEntity(ActionType actionType, BatchAuditImport item) {
        CsvFileRow row = item.getCsvFileRow();
        Provider provider = providerUsecase.getProviderByCode(row.getProviderCode());
        if(provider != null)
            return provider;
        else return Provider.builder()
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