package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;

import static br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus.AUDIT_PERSISTED;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.DateUtils.YYYY_MM_DD_ToLocalDate;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class AuditProcessor implements ItemProcessor<CsvFileRow, BatchAuditImport> {

    private StepExecution stepExecution;

    @Override
    public BatchAuditImport process(CsvFileRow item) {

        LocalDate lastDbDate = (LocalDate) stepExecution.getExecutionContext().get("lastDbDate");
        LocalDate actionDate = YYYY_MM_DD_ToLocalDate(item.getTransactionDate());
        if (actionDate != null && lastDbDate != null && !actionDate.isAfter(lastDbDate)) return null;

        return BatchAuditImport.builder()
                .actionFileDate(YYYY_MM_DD_ToLocalDate(item.getTransactionDate()))
                .csvFileRow(item)
                .changesHistory(new ArrayList<>())
                .status(AUDIT_PERSISTED)
                .build();
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

}