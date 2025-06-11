package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionDuplicationValidationImpl implements TransactionValidation {
    private final TransactionUsecase transactionUsecase;

    public TransactionDuplicationValidationImpl(TransactionUsecase transactionUsecase) {
        this.transactionUsecase = transactionUsecase;
    }

    @Override
    public void validate(Card card, BigDecimal amount, TransactionType transactionType) {
        transactionUsecase.isDuplicateTransaction(card, amount, transactionType);
    }
}