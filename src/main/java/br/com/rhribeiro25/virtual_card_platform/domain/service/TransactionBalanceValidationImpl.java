package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionBalanceValidationImpl implements TransactionValidation {
    @Override
    public void validate(Card card, BigDecimal amount, TransactionType transactionType) {
        if (transactionType == TransactionType.SPEND && card.getBalance().compareTo(amount) < 0)
            throw new BadRequestException(MessageUtil.getMessage("card.insufficientBalance"));
    }
}
