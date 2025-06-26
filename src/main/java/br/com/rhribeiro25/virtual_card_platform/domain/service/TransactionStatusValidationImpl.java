package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.stereotype.Service;

@Service
public class TransactionStatusValidationImpl implements TransactionValidation {
    public void validate(Transaction transaction) {
        if (transaction.getCard().getStatus() == CardStatus.BLOCKED)
            throw new BadRequestException(MessageUtil.getMessage("card.blocked.message"));
    }

    @Override
    public boolean supports(TransactionType transactionType) {
        return transactionType.equals(TransactionType.TOPUP)
                || transactionType.equals(TransactionType.SPEND);
    }
}