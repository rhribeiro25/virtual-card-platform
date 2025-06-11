package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;

import java.math.BigDecimal;

public interface TransactionValidation {
    void validate(Card card, BigDecimal amount, TransactionType transactionType);
}
