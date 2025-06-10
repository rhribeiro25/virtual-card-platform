package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.mapper.TransactionMapper;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.repository.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.utils.MessageUtil;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    @Autowired
    public CardService(CardRepository cardRepository, TransactionService transactionService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public Card create(Card card) {
        try {
            return cardRepository.save(card);
        } catch (OptimisticLockException e) {
            throw new BadRequestException(MessageUtil.getMessage("card.conflict"));
        }
    }

    @Transactional
    public Card spend(UUID cardId, BigDecimal amount) {

        long recentSpends = transactionService.countRecentSpends(cardId);
        int spendLimitPerMinute = Integer.parseInt(MessageUtil.getMessage("card.spend.limitPerMinute"));

        if (recentSpends >= spendLimitPerMinute) {
            throw new BadRequestException(MessageUtil.getMessage("card.spend.rateLimit", spendLimitPerMinute));
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException(MessageUtil.getMessage("card.blocked.message"));
        }

        transactionService.isDuplicateTransaction(card, amount, TransactionType.SPEND);

        if (card.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException(MessageUtil.getMessage("card.insufficientBalance"));
        }
        card.setBalance(card.getBalance().subtract(amount));
        try {
            cardRepository.save(card);
        } catch (OptimisticLockException e) {
            throw new BadRequestException(MessageUtil.getMessage("card.conflict"));
        }

        transactionService.create(TransactionMapper.toEntity(amount, card, TransactionType.SPEND));

        return card;
    }

    @Transactional
    public Card topUp(UUID cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));

        transactionService.isDuplicateTransaction(card, amount, TransactionType.TOPUP);

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        transactionService.create(TransactionMapper.toEntity(amount, card, TransactionType.TOPUP));

        try {
            return cardRepository.save(card);
        } catch (OptimisticLockException e) {
            throw new BadRequestException(MessageUtil.getMessage("card.conflict"));
        }
    }

    public Card getCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));
    }

    public Page<Transaction> getTransactionsByValidCardId(UUID cardId, Pageable pageable) {
        getCardById(cardId);
        return transactionService.getTransactionsByCardId(cardId, pageable);
    }

}
