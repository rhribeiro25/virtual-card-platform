package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImportHistory;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportMongoTemplate;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BatchErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus.*;
import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.RESOURCE;

@Slf4j
@Component
public class GenericChunkListener implements ChunkListener, SkipListener<BatchAuditImport, BatchAuditImport> {

    @Autowired
    private BatchAuditImportMongoTemplate batchAuditImportMongoTemplate;

    @Override
    public void afterChunk(ChunkContext context) {
        StepExecution stepExecution =
                context.getStepContext().getStepExecution();

        Long readCount = stepExecution.getReadCount();
        Long writeCount = stepExecution.getWriteCount();
        Long readSkip = stepExecution.getReadSkipCount();
        Long processSkip = stepExecution.getProcessSkipCount();
        Long writeSkip = stepExecution.getWriteSkipCount();

        log.info("""
                        Chunk finished
                        Step: {}
                        Total Read: {}
                        Total Write: {}
                        Read Skip: {}
                        Process Skip: {}
                        Write Skip: {}
                        """,
                stepExecution.getStepName(),
                readCount,
                writeCount,
                readSkip,
                processSkip,
                writeSkip
        );
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        Throwable t = context.getAttribute(ChunkListener.ROLLBACK_EXCEPTION_KEY) instanceof Throwable th ? th : null;
        log.error("Chunk failed: {}", BatchErrorException.formatThrowable(t), t);
    }

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("SKIP on READ | {}", enrichWithInputFile(t), t);
    }

    @Override
    public void onSkipInProcess(BatchAuditImport item, Throwable t) {

        log.error("SKIP on PROCESS | item={} | {}", safeItem(item), enrichWithInputFile(t), t);

        String stepName = getStepNameSafe();
        BatchAuditImportStatus status = null;
        if (stepName.contains("cardStep"))
            status = CARD_ERROR;
        else if (stepName.contains("providerStep"))
            status = PROVIDER_ERROR;
        else if (stepName.contains("cardProviderStep"))
            status = CARD_PROVIDER_ERROR;
        else if (stepName.contains("transactionStep"))
            status = TRANSACTION_ERROR;

        var lastHistory = BatchAuditImportHistory.builder()
                .status(status)
                .build();
        item.getChangesHistory().add(lastHistory);

        batchAuditImportMongoTemplate.update(
                item.getId(),
                item.getStatus(),
                item.getChangesHistory()
        );
    }

    @Override
    public void onSkipInWrite(BatchAuditImport item, Throwable t) {
        log.error("SKIP on WRITE | item={} | {}", safeItem(item), enrichWithInputFile(t), t);
    }

    private String enrichWithInputFile(Throwable t) {
        String base = BatchErrorException.formatThrowable(t);

        var stepCtx = StepSynchronizationManager.getContext();
        if (stepCtx == null) return base;

        String inputFile = stepCtx.getStepExecution()
                .getExecutionContext()
                .getString(RESOURCE.getPath(), null);

        return (inputFile == null) ? base : (base + " | inputFile=" + inputFile);
    }

    private String safeItem(Object item) {
        if (item == null) return "null";
        String s = item.toString();
        return s.length() > 800 ? s.substring(0, 800) + "..." : s;
    }

    private String getStepNameSafe() {
        var ctx = StepSynchronizationManager.getContext();
        if (ctx == null || ctx.getStepExecution() == null) return "unknown";
        return ctx.getStepExecution().getStepName();
    }
}
