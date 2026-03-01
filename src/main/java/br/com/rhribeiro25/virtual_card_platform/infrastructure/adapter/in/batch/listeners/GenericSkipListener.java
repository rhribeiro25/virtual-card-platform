package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.BatchAuditImportUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImportHistory;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class GenericSkipListener implements StepExecutionListener, SkipListener<BatchAuditImport, BatchAuditImport> {

    private final BatchAuditImportUsecase batchAuditImportUsecase;
    private String stepName;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepName = stepExecution.getStepName();
    }

    @Override
    public void onSkipInRead(Throwable t) {
        log.error(formatingError(t));
    }

    @Override
    public void onSkipInProcess(BatchAuditImport item, Throwable t) {
        settingBatchAuditImport(item, t);
    }

    @Override
    public void onSkipInWrite(BatchAuditImport item, Throwable t) {
        settingBatchAuditImport(item, t);
    }

    private void settingBatchAuditImport(BatchAuditImport item, Throwable t) {
        item.setStatus(BatchAuditImportStatus.getStatusError(stepName));
        BatchAuditImportHistory auditImportHistory = BatchAuditImportHistory.builder()
                .status(item.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .error(formatingError(t))
                .build();
        item.getChangesHistory().add(auditImportHistory);
        batchAuditImportUsecase.update(item);
    }

    private String formatingError(Throwable e) {

        List<StackTraceElement> stackTraceList = Arrays.stream(e.getStackTrace()).toList();
        StackTraceElement stackTrace = stackTraceList.stream()
                .filter(st -> st.getClassName().startsWith("br.com.rhribeiro25.virtual_card_platform")).findFirst().orElse(null);

        if (stackTrace != null) return String.format(
                "[MESSAGE] - %s \n[CLASS] - %s \n[LOCATION] - %s : %d",
                e.getMessage(),
                stackTrace.getClassName(), stackTrace.getMethodName(), stackTrace.getLineNumber());

        else return null;
    }

}