package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;

import java.math.BigDecimal;

public interface TransactionValidation {
    void validate(Card card, BigDecimal amount, TransactionType transactionType);
    default boolean supports(String transactionType) { return true; }
}
