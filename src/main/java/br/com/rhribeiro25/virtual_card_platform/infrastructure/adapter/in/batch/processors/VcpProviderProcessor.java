package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ProviderStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.application.dto.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.application.dto.CsvRow;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.JobScopeCacheUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.StepScopeCacheUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpProviderProcessor implements ItemProcessor<AuditImport, Provider> {

    private final JobScopeCacheUtils jobScopeCacheUtils;
    private final StepScopeCacheUtils stepScopeCacheUtils;
    private final ObjectMapper objectMapper;

    @Override
    public Provider process(AuditImport auditImport) throws JsonProcessingException {

        CsvRow csvRow = objectMapper.readValue(auditImport.getRawPayload(), CsvRow.class);

        Provider provider = jobScopeCacheUtils.providerCache().get(csvRow.getProviderCode());
        if (provider == null) {
            provider = Provider.builder()
                    .code(csvRow.getProviderCode())
                    .createdAt(LocalDateTime.now())
                    .status(mapProviderStatus(csvRow.getProviderState()))
                    .country(csvRow.getProviderCountry())
                    .build();
        }
        stepScopeCacheUtils.auditCache().put(auditImport.getId().toString(), auditImport);
        return provider;
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