package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.AbstractModel;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class VcpAbstractBatchWriter<T, K> implements ItemWriter<BatchAuditImport> {

    private final BatchAuditMongoTemplate batchAuditMongoTemplate;

    protected VcpAbstractBatchWriter(BatchAuditMongoTemplate batchAuditMongoTemplate) {
        this.batchAuditMongoTemplate = batchAuditMongoTemplate;
    }

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        log.info("Starting: {}", writerName());
        for (BatchAuditImport item : chunk.getItems()) {
            T entity = extractEntity(item);
            if (entity != null) {
                K key = extractKey(entity);
                Optional<UUID> entityId = findExistingEntityIdByKey(key);
                if (entityId.isEmpty()) {
                    save(entity);
                    entityId = Optional.of(((AbstractModel) entity).getId());
                }
                batchAuditMongoTemplate.updatePersistedEntityId(item.getId(), entityId.get(), getField());
            }
        }
    }

    protected abstract String writerName();

    protected abstract T extractEntity(BatchAuditImport item);

    protected abstract K extractKey(T entity);

    protected abstract void save(T entity);

    protected abstract String getField();

    protected abstract Optional<UUID> findExistingEntityIdByKey(K key);
}
