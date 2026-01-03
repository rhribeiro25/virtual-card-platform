package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component(SpringBatchProcessor.AUDIT)
@StepScope
@RequiredArgsConstructor
public class VcpAuditProcessor implements ItemProcessor<CsvFileRow, BatchAuditImport> {

    @Override
    public BatchAuditImport process(CsvFileRow item) throws JsonProcessingException {
        UUID providerCode = UUID.randomUUID();
        item.setProviderCode(providerCode.toString());
        return BatchAuditImport.builder()
                .id(UUID.randomUUID())
                .cardRef(item.getCardRef())
                .providerCode(providerCode.toString())
                .txRequestRef(item.getTxRequestRef() != null ? UUID.fromString(item.getTxRequestRef()) : null)
                .csvFileRow(item)
                .isProcessedCard(false)
                .isProcessedProvider(false)
                .isProcessedCardProvider(false)
                .isProcessedTransaction(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

}