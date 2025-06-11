package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionDuplicationValidationImpl implements TransactionValidation {
    private final TransactionService transactionService;

    public TransactionDuplicationValidationImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void validate(Card card, BigDecimal amount, TransactionType transactionType) {
        transactionService.isDuplicateTransaction(card, amount, transactionType);
    }
}