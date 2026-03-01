package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.AbstractModel;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImportHistory;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportMongoTemplate;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.PersistenceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract Spring Batch {@link ItemWriter} implementation designed to safely handle
 * create and update operations for domain entities within a single chunk.
 * <p>
 * This writer ensures consistency and avoids optimistic locking conflicts by
 * caching and reusing managed JPA entity instances during chunk processing.
 * Multiple records referring to the same business key are merged into the same
 * managed entity before persistence.
 *
 * <p><b>Key responsibilities:</b>
 * <ul>
 *   <li>Resolve entities by a business key within the current chunk</li>
 *   <li>Reuse managed entities to prevent version conflicts</li>
 *   <li>Merge incoming data into existing entities when applicable</li>
 *   <li>Persist entities in a consistent and idempotent manner</li>
 *   <li>Update audit metadata with the persisted entity identifier</li>
 * </ul>
 *
 * <p>Implementations must ensure that existing entities returned from persistence
 * lookups are managed within the current transactional context.
 *
 * @param <E> the domain entity type, extending {@link AbstractModel}
 * @param <K> the business key type used to identify unique entities
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractBatchWriter<E extends AbstractModel, K>
        implements ItemWriter<BatchAuditImport> {

    private final BatchAuditImportMongoTemplate batchAuditImportMongoTemplate;
    private final PersistenceUtils persistenceUtils;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        log.info("Starting writer: {}", getWriterName());

        /**
         * In-memory cache scoped to the current chunk.
         *
         * Purpose:
         * - Ensure that multiple records referring to the same business key
         *   are merged into a single managed entity instance.
         * - Prevent OptimisticLockException caused by saving multiple detached
         *   instances of the same entity within the same transaction.
         */
        Map<K, E> entityCache = new HashMap<>();

        for (BatchAuditImport item : chunk.getItems()) {

            // Extract the domain entity from the batch item
            E incoming = extractEntity(item);
            if (incoming == null) continue;

            // Extract the business key used to uniquely identify the entity
            K key = extractKey(incoming);

            E managedEntity;

            createLastBatchAuditImportHistory(item);

            if (entityCache.containsKey(key)) {
                /**
                 * Case 1:
                 * The entity was already processed earlier in the same chunk.
                 * Reuse the managed instance and merge new values into it.
                 */
                managedEntity = entityCache.get(key);
                if (!managedEntity.equals(incoming)) {
                    mergeEntities(managedEntity, incoming);
                    setAuditStatus(item, true);
                } else setAuditStatus(item, null);
            } else {
                /**
                 * Case 2:
                 * First occurrence of this business key in the chunk.
                 * Attempt to load an existing entity from the database.
                 */
                Optional<E> existing = findExistingEntityByKey(key);

                if (existing.isPresent()) {
                    /**
                     * Existing entity found in the database.
                     * Merge incoming values into the managed JPA entity.
                     */
                    managedEntity = existing.get();
                    if (!managedEntity.equals(incoming)) {
                        mergeEntities(managedEntity, incoming);
                        setAuditStatus(item, true);
                    } else setAuditStatus(item, null);
                } else {
                    /**
                     * No entity exists yet.
                     * The incoming entity will be treated as a new aggregate.
                     */
                    managedEntity = incoming;
                    setAuditStatus(item, false);
                }
            }
            // Cache the managed entity to be reused for the rest of the chunk
            entityCache.put(key, managedEntity);
            /**
             * Persist the managed entity.
             * JPA will decide whether this is an INSERT or UPDATE.
             */
            save(managedEntity);

            /**
             * Update audit information with the persisted entity ID.
             * This ensures traceability between the batch input row
             * and the resulting database record.
             */
            batchAuditImportMongoTemplate.update(
                    item.getId(),
                    managedEntity.getId(),
                    getField(),
                    item.getStatus(),
                    item.getChangesHistory()
            );
        }
        persistenceUtils.flushAndClear();
    }

    /**
     * @return logical writer name used for logging and monitoring
     */
    protected abstract String getWriterName();

    /**
     * Extracts the domain entity from the batch input item
     */
    protected abstract E extractEntity(BatchAuditImport item);

    /**
     * Extracts the business key used to uniquely identify an entity
     */
    protected abstract K extractKey(E entity);

    /**
     * Persists the entity (insert or update)
     */
    protected abstract void save(E entity);

    /**
     * @return the audit field name to be updated
     */
    protected abstract String getField();

    /**
     * Loads an existing entity using its business key
     */
    protected abstract Optional<E> findExistingEntityByKey(K key);

    /**
     * Merges incoming values into an existing managed entity
     */
    protected abstract void mergeEntities(E existing, E incoming);

    /**
     * Setting BatchAuditImportStatus in BatchAuditImport
     */
    protected abstract void setAuditStatus(BatchAuditImport item, Boolean exists);

    /**
     * Creating last step BatchAuditImportHistory
     */
    protected void createLastBatchAuditImportHistory(BatchAuditImport item) {
        var lastHistory = BatchAuditImportHistory.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(item.getStatus())
                .build();
        item.getChangesHistory().add(lastHistory);
    }
}
