package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@SuperBuilder
public abstract class VcpAbstractBatchWriter<T, K> implements ItemWriter<BatchAuditImport> {

    private final BatchAuditMongoTemplate batchAuditMongoTemplate;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        log.info("Starting: {}", writerName());

        Set<K> chunkCheck = new HashSet<>();

        for (BatchAuditImport item : chunk.getItems()) {
            item.setIsTransientEntitySaved(true);

            T entity = extractEntity(item);
            if (entity != null) {
                K key = extractKey(entity);

                if (!chunkCheck.contains(key)) {
                    try {
                        chunkCheck.add(key);
                        save(entity);
                    } catch (DataIntegrityViolationException e) {
                        item.setIsTransientEntitySaved(false);
                        log.warn("{} already exists: {}", writerName(), item.getId());
                    }
                }
            }

            batchAuditMongoTemplate.updateProcessedFlag(item, processedFlag());
        }
    }

    protected abstract String writerName();

    protected abstract T extractEntity(BatchAuditImport item);

    protected abstract K extractKey(T entity);

    protected abstract void save(T entity);

    protected abstract String processedFlag();
}
