package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos;

import lombok.Getter;

@Getter
public enum AuditImportProcessedStep {

    AUDIT("auditStep", "cardStep"),
    CARD("cardStep", "providerStep"),
    PROVIDER("providerStep", "cardProviderStep"),
    CARD_PROVIDER("cardProviderStep", "transactionStep"),
    TRANSACTION("transactionStep", "transactionStep"); // último step

    private final String stepName;
    private final String nextStep;

    AuditImportProcessedStep(String stepName, String nextStep) {
        this.stepName = stepName;
        this.nextStep = nextStep;
    }

    public static AuditImportProcessedStep fromStepName(String stepName) {
        for (AuditImportProcessedStep step : values()) {
            if (step.getStepName().equals(stepName)) {
                return step;
            }
        }
        throw new IllegalArgumentException("Unknown step name: " + stepName);
    }
}
