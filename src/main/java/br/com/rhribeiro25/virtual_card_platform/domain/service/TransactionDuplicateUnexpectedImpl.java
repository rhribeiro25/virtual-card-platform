package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.CacheUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
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
        UUID cardId = transaction.getCard().getId();
        boolean existsInCache = false;
        boolean existsInDb = false;

        if (requestId == null) {
            return;
        }
        Page<Transaction> transactionsPage = CacheUtils.getFromCache("transactionsByCardId", cardId, Page.class);
        if (transactionsPage != null) {
            existsInCache = transactionsPage.getContent().stream()
                    .anyMatch(t -> cardId.equals(t.getCard().getId()) &&
                            requestId.equals(t.getRequestId()));
        } else {
            existsInDb = transactionUsecase
                    .verifyDuplicateTransaction(cardId, requestId)
                    .isPresent();
        }
        if (existsInCache || existsInDb) {
            throw new ConflictException(MessageUtils.getMessage("card.duplicateTransaction"));
        }
    }


    @Override
    public boolean supports(TransactionType transactionType) {
        return transactionType.equals(TransactionType.TOPUP)
                || transactionType.equals(TransactionType.SPEND);
    }
}