package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos;

import lombok.Getter;

@Getter
public enum AuditImportStep {

    AUDIT("auditStep"),
    CARD("cardStep"),
    PROVIDER("providerStep"),
    CARD_PROVIDER("cardProviderStep"),
    TRANSACTION("transactionStep"); // último step

    private final String name;

    AuditImportStep(String name) {
        this.name = name;
    }

    public static AuditImportStep getByName(String stepName) {
        for (AuditImportStep step : values()) {
            if (step.getName().equals(stepName)) {
                return step;
            }
        }
        throw new IllegalArgumentException("Unknown step name: " + stepName);
    }
}
