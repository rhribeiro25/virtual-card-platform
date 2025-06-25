package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionLimitValidationImpl implements TransactionValidation {
    private final TransactionUsecase transactionUsecase;

    public TransactionLimitValidationImpl(TransactionUsecase transactionUsecase) {
        this.transactionUsecase = transactionUsecase;
    }

    @Override
    public void validate(Card card, BigDecimal amount, TransactionType transactionType) {
        long recentSpends = transactionUsecase.countRecentSpends(card.getId(), transactionType);
        int spendLimitPerMinute = Integer.parseInt(MessageUtil.getMessage("card.spend.limitPerMinute"));

        if (recentSpends >= spendLimitPerMinute) {
            throw new BadRequestException(MessageUtil.getMessage("card.spend.rateLimit", spendLimitPerMinute));
        }
    }

    @Override
    public boolean supports(String transactionType) {
        return transactionType.equalsIgnoreCase("SPEND");
    }
}