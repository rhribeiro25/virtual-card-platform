package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.stereotype.Service;

@Service
public class TransactionBalanceValidationImpl implements TransactionValidation {

    @Override
    public void validate(Transaction transaction) {
        if (transaction.getType() == TransactionType.SPEND && transaction.getCard().getBalance().compareTo(transaction.getAmount()) < 0)
            throw new BadRequestException(MessageUtil.getMessage("card.insufficientBalance"));
    }

    @Override
    public boolean supports(TransactionType transactionType) {
        return transactionType.equals(TransactionType.SPEND);
    }
}
