package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ProviderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component(SpringBatchProcessor.PROVIDER)
@StepScope
@RequiredArgsConstructor
public class VcpProviderProcessor implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    private final ProviderUsecase providerUsecase;

    @Override
    public BatchAuditImport process(BatchAuditImport batchAuditImport) throws JsonProcessingException {

        CsvFileRow csvFileRow = batchAuditImport.getCsvFileRow();
        if (providerUsecase.existsByCode(csvFileRow.getProviderCode())) return batchAuditImport;

        batchAuditImport.setProvider(Provider.builder()
                .code(csvFileRow.getProviderCode())
                .createdAt(LocalDateTime.now())
                .status(mapProviderStatus(csvFileRow.getProviderState()))
                .country(csvFileRow.getProviderCountry())
                .build());

        return batchAuditImport;
    }

    private ProviderStatus mapProviderStatus(String state) {

        return switch (state) {
            case "A" -> ProviderStatus.ACTIVE;
            case "B" -> ProviderStatus.BLOCKED;
            default -> throw new IllegalArgumentException(
                    "Invalid Provider: " + state
            );
        };
    }

}