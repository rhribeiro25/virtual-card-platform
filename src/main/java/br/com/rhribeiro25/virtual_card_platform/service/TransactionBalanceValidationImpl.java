package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.utils.MessageUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionBalanceValidationImpl implements TransactionValidation {
    @Override
    public void validate(Card card, BigDecimal amount, TransactionType transactionType) {
        if (amount.signum() <= 0)
            throw new BadRequestException(MessageUtil.getMessage("Invalid.transaction.amount"));

        if (transactionType == TransactionType.SPEND && card.getBalance().compareTo(amount) < 0)
            throw new BadRequestException(MessageUtil.getMessage("card.insufficientBalance"));
    }
}
