package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.CsvRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpAuditProcessor implements ItemProcessor<CsvRow, AuditImport> {

    private final ObjectMapper objectMapper;

    @Override
    public AuditImport process(CsvRow item) throws JsonProcessingException {

        return AuditImport.builder()
                .id(UUID.randomUUID())
                .cardRef(item.getCardRef())
                .providerCode(item.getProviderCode())
                .txRequestRef(item.getTxRequestRef() != null ? UUID.fromString(item.getTxRequestRef()) : null)
                .rawPayload(objectMapper.writeValueAsString(item))
                .build();
    }

}