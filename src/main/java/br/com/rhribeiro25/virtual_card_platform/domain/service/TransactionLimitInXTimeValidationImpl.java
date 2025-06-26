package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.stereotype.Service;

@Service
public class TransactionLimitInXTimeValidationImpl implements TransactionValidation {
    private final TransactionUsecase transactionUsecase;

    public TransactionLimitInXTimeValidationImpl(TransactionUsecase transactionUsecase) {
        this.transactionUsecase = transactionUsecase;
    }

    @Override
    public void validate(Transaction transaction) {
        long recentSpends = transactionUsecase.countRecentSpends(transaction.getCard().getId(), transaction.getType());
        int spendLimitPerMinute = Integer.parseInt(MessageUtil.getMessage("card.spend.limitPerMinute"));

        if (recentSpends >= spendLimitPerMinute) {
            throw new BadRequestException(MessageUtil.getMessage("card.spend.rateLimit", spendLimitPerMinute));
        }
    }

    @Override
    public boolean supports(TransactionType transactionType) {
        return transactionType.equals(TransactionType.SPEND);
    }
}