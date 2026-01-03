package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchWriter;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component(SpringBatchWriter.AUDIT)
@StepScope
@RequiredArgsConstructor
public class VcpAuditWriter implements ItemWriter<BatchAuditImport> {

    private final BatchAuditImportRepository batchAuditImportRepository;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) throws Exception {
        batchAuditImportRepository.saveAll(chunk.getItems());
    }
}
