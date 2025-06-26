package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionDuplicateUnexpectedImpl implements TransactionValidation {
    private final TransactionUsecase transactionUsecase;

    public TransactionDuplicateUnexpectedImpl(TransactionUsecase transactionUsecase) {
        this.transactionUsecase = transactionUsecase;
    }

    @Override
    public void validate(Transaction transaction) {
        UUID requestId = transaction.getRequestId();
        Optional<Transaction> optionalTransaction = transactionUsecase.verifyDuplicateTransaction(transaction.getCard().getId(), requestId);
        if (optionalTransaction.isPresent()) {
            throw new ConflictException(MessageUtil.getMessage("card.duplicateTransaction"));
        }
    }

    @Override
    public boolean supports(TransactionType transactionType) {
        return transactionType.equals(TransactionType.TOPUP)
                || transactionType.equals(TransactionType.SPEND);
    }
}