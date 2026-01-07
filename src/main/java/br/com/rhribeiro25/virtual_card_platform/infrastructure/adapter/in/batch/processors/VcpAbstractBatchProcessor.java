package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public abstract class VcpAbstractBatchProcessor<T> implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    @Override
    public BatchAuditImport process(BatchAuditImport item) throws Exception {

        CsvFileRow row = item.getCsvFileRow();

        if (shouldSkip(row, item)) {
            return item;
        }

        if (!dependenciesResolved(item)) {
            return null;
        }

        T entity = buildEntity(row, item);

        if (entity == null) {
            return null;
        }

        attachEntity(item, entity);
        return item;
    }

    protected abstract boolean shouldSkip(CsvFileRow row, BatchAuditImport item);

    protected abstract boolean dependenciesResolved(BatchAuditImport item);

    protected abstract T buildEntity(CsvFileRow row, BatchAuditImport item);

    protected abstract void attachEntity(BatchAuditImport item, T entity);
}
