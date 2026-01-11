package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public abstract class VcpAbstractBatchProcessor<T> implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    @Override
    public BatchAuditImport process(BatchAuditImport item) {

        ActionType actionType = ActionType.valueOf(item.getCsvFileRow().getActionType());
        if (!dependenciesResolved(item)) {
            return null;
        }
        T entity = buildEntity(actionType, item);
        if (entity == null) {
            return null;
        }
        attachEntity(item, entity);
        return item;
    }

    protected abstract boolean dependenciesResolved(BatchAuditImport item);

    protected abstract T buildEntity(ActionType actionType, BatchAuditImport item);

    protected abstract void attachEntity(BatchAuditImport item, T entity);
}
