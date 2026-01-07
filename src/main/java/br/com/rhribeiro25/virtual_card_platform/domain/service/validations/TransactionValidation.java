package br.com.rhribeiro25.virtual_card_platform.domain.service.validations;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;

public interface TransactionValidation {
    void validate(Transaction transaction);
    default boolean supports(TransactionType transactionType) { return true; }
}
