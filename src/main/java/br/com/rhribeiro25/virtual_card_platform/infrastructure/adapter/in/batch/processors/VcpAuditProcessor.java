package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component(SpringBatchProcessor.AUDIT)
@StepScope
@RequiredArgsConstructor
public class VcpAuditProcessor implements ItemProcessor<CsvFileRow, BatchAuditImport> {

    @Override
    public BatchAuditImport process(CsvFileRow item) {

        return BatchAuditImport.builder()
                .cardRef(item.getCardRef())
                .providerCode(item.getProviderCode())
                .txRequestRef(item.getTxRequestRef())
                .csvFileRow(item)
                .createdAt(LocalDateTime.now())
                .build();
    }

}