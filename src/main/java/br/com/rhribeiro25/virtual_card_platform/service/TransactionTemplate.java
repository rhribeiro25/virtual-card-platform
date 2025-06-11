package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.mapper.TransactionMapper;
import br.com.rhribeiro25.virtual_card_platform.model.*;
import br.com.rhribeiro25.virtual_card_platform.repository.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.utils.MessageUtil;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public abstract class TransactionTemplate {

    @Autowired
    protected CardRepository cardRepository;

    @Autowired
    protected TransactionService transactionService;

    public final Card process(Card card, BigDecimal amount) {
        validate(card, amount);
        updateBalance(card, amount);
        saveCard(card);
        createTransaction(card, amount);
        return card;
    }

    protected abstract void validate(Card card, BigDecimal amount);
    protected abstract void updateBalance(Card card, BigDecimal amount);
    protected abstract TransactionType getType();

    private void saveCard(Card card) {
        try {
            cardRepository.save(card);
        } catch (OptimisticLockException e) {
            throw new BadRequestException(MessageUtil.getMessage("card.conflict"));
        }
    }

    private void createTransaction(Card card, BigDecimal amount) {
        transactionService.create(TransactionMapper.toEntity(amount, card, getType()));
    }
}
