package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.utils.MessageUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionStatusValidationImpl implements TransactionValidation {
    public void validate(Card card, BigDecimal amount, TransactionType transactionType) {
        if (card.getStatus() == CardStatus.BLOCKED)
            throw new BadRequestException(MessageUtil.getMessage("card.blocked.message"));
    }
}