package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.utils.MessageUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionLimitValidationImpl implements TransactionValidation {
    private final TransactionService transactionService;

    public TransactionLimitValidationImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void validate(Card card, BigDecimal amount, TransactionType transactionType) {
        long recentSpends = transactionService.countRecentSpends(card.getId(), transactionType);
        int spendLimitPerMinute = Integer.parseInt(MessageUtil.getMessage("card.spend.limitPerMinute"));

        if (recentSpends >= spendLimitPerMinute) {
            throw new BadRequestException(MessageUtil.getMessage("card.spend.rateLimit", spendLimitPerMinute));
        }
    }
}