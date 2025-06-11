package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;

import java.math.BigDecimal;

public interface TransactionValidation {
    void validate(Card card, BigDecimal amount, TransactionType transactionType);
}
