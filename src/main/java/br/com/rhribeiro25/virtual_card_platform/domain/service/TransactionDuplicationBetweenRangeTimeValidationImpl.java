package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionDuplicationBetweenRangeTimeValidationImpl implements TransactionValidation {
    private final TransactionUsecase transactionUsecase;

    public TransactionDuplicationBetweenRangeTimeValidationImpl(TransactionUsecase transactionUsecase) {
        this.transactionUsecase = transactionUsecase;
    }

    @Override
    public void validate(Transaction transaction) {
        transactionUsecase.isDuplicateTransaction(transaction.getCard(), transaction.getAmount(), transaction.getType());
    }

    @Override
    public boolean supports(TransactionType transactionType) {
        //disabled
        return false;
    }
}