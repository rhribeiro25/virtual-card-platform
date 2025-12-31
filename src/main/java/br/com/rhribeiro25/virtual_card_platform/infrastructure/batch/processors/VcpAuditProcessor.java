package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditVcpRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.VirtualCardsCsvRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpAuditProcessor implements ItemProcessor<VirtualCardsCsvRow, AuditVcpRow> {

    private final ObjectMapper objectMapper;

    @Override
    public AuditVcpRow process(VirtualCardsCsvRow item) throws JsonProcessingException {

        return AuditVcpRow.builder()
                .id(UUID.randomUUID())
                .cardRef(item.getCardRef())
                .providerCode(item.getProviderCode())
                .txRequestRef(item.getTxRequestRef() != null ? UUID.fromString(item.getTxRequestRef()) : null)
                .rawPayload(objectMapper.writeValueAsString(item))
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();
    }

}