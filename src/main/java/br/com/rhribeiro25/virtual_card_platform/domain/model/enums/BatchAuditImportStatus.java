package br.com.rhribeiro25.virtual_card_platform.domain.model.enums;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.*;

public enum BatchAuditImportStatus {
    AUDIT_PERSISTED,

    CARD_PERSISTED,
    CARD_UPDATED,
    CARD_NO_ACTION,
    CARD_ERROR,

    PROVIDER_PERSISTED,
    PROVIDER_UPDATED,
    PROVIDER_NO_ACTION,
    PROVIDER_ERROR,

    CARD_PROVIDER_PERSISTED,
    CARD_PROVIDER_UPDATED,
    CARD_PROVIDER_NO_ACTION,
    CARD_PROVIDER_ERROR,

    TRANSACTION_PERSISTED,
    TRANSACTION_UPDATED,
    TRANSACTION_NO_ACTION,
    TRANSACTION_ERROR,

    SUCCESSFUL;

    public static BatchAuditImportStatus getStatusError(String stepName) {
        if (stepName.equalsIgnoreCase(CARD_STEP)) return CARD_ERROR;
        else if (stepName.equalsIgnoreCase(PROVIDER_STEP)) return PROVIDER_ERROR;
        else if (stepName.equalsIgnoreCase(CARD_PROVIDER_STEP)) return CARD_PROVIDER_ERROR;
        else if (stepName.equalsIgnoreCase(TRANSACTION_STEP)) return TRANSACTION_ERROR;
        else return null;
    }
}


