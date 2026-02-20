package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.List;

import static br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus.*;
import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.QUERY_MONGO_AUDIT;

@Component
@RequiredArgsConstructor
@StepScope
public class GenericStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {

        String stepName = stepExecution.getStepName();
        List<String> statuses;

        if (stepName.equalsIgnoreCase("cardStep"))
            statuses = List.of(AUDIT_PERSISTED.name(), CARD_ERROR.name());
        else if (stepName.equalsIgnoreCase("providerStep"))
            statuses = List.of(CARD_PERSISTED.name(), CARD_UPDATED.name(), CARD_NO_ACTION.name(), PROVIDER_ERROR.name());
        else if (stepName.equalsIgnoreCase("cardProviderStep"))
            statuses = List.of(PROVIDER_PERSISTED.name(), PROVIDER_UPDATED.name(), PROVIDER_NO_ACTION.name(), CARD_PROVIDER_ERROR.name());
        else if (stepName.equalsIgnoreCase("transactionStep"))
            statuses = List.of(CARD_PROVIDER_PERSISTED.name(), CARD_PROVIDER_UPDATED.name(), CARD_PROVIDER_NO_ACTION.name(), TRANSACTION_ERROR.name());
        else statuses = List.of();

        String whereMongo = buildStatusInQuery(statuses);
        stepExecution.getExecutionContext().putString(QUERY_MONGO_AUDIT, whereMongo);
    }

    private String buildStatusInQuery(List<String> statuses) {

        if (statuses == null || statuses.isEmpty())
            return "{}";

        String jsonArray = statuses.stream()
                .map(s -> "\"" + s + "\"")
                .reduce((a, b) -> a + "," + b)
                .map(s -> "[" + s + "]")
                .orElse("[]");

        return """
                { "status": { "$in": %s } }
                """.formatted(jsonArray);
    }
}