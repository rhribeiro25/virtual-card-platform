package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchWriter;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpringBatchWriter.AUDIT)
@StepScope
@RequiredArgsConstructor
public class VcpAuditWriter implements ItemWriter<BatchAuditImport> {

    private final BatchAuditImportRepository batchAuditImportRepository;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        log.info("Starting: {}", SpringBatchWriter.AUDIT);
        batchAuditImportRepository.saveAll(chunk.getItems());
    }
}
