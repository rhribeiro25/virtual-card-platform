package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.BatchAuditImportUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.LAST_TRANSACTION_DATE;

@Component
@RequiredArgsConstructor
@StepScope
public class AuditStepListener implements StepExecutionListener {

    private final BatchAuditImportUsecase batchAuditImportUsecase;

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        LocalDate lastDbDate = batchAuditImportUsecase
                .findFirstByOrderByActionFileDateDesc()
                .map(BatchAuditImport::getActionFileDate)
                .orElse(null);
        stepExecution.getExecutionContext().put(LAST_TRANSACTION_DATE, lastDbDate);
    }
}