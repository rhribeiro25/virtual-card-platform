package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.repository.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.utils.MessageUtil;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    @Value("${card.conflict}")
    private String conflictMessage;

    @Value("${card.notFound}")
    private String notFoundMessage;

    @Value("${card.insufficientBalance}")
    private String insufficientBalanceMessage;

    @Value("${card.spend.limitPerMinute}")
    private int spendLimitPerMinute;

    @Value("${card.spend.rateLimit}")
    private String spendRateLimitMessage;

    @Autowired
    public CardService(CardRepository cardRepository, TransactionService transactionService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public Card create(String cardholderName, BigDecimal initialBalance) {
        Card card = new Card.Builder()
                .cardholderName(cardholderName)
                .balance(initialBalance)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        try {
            return cardRepository.save(card);
        } catch (OptimisticLockException e) {
            throw new BadRequestException(conflictMessage);
        }
    }

    @Transactional
    public Card spend(UUID cardId, BigDecimal amount) {

        long recentSpends = transactionService.countRecentSpends(cardId);

        if (recentSpends >= spendLimitPerMinute) {
            throw new BadRequestException(MessageUtil.getMessage("card.spend.rateLimit", spendLimitPerMinute));
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(notFoundMessage));

        transactionService.isDuplicateTransaction(card, amount);

        if (card.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException(insufficientBalanceMessage);
        }

        card.setBalance(card.getBalance().subtract(amount));

        try {
            cardRepository.save(card);
        } catch (OptimisticLockException e) {
            throw new BadRequestException(conflictMessage);
        }

        transactionService.create(new Transaction.Builder()
                .card(card)
                .type(TransactionType.SPEND)
                .amount(amount)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build());

        return card;
    }

    @Transactional
    public Card topUp(UUID cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(notFoundMessage));

        transactionService.isDuplicateTransaction(card, amount);

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        transactionService.create(new Transaction.Builder()
                .card(card)
                .type(TransactionType.TOPUP)
                .amount(amount)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build());

        try {
            return cardRepository.save(card);
        } catch (OptimisticLockException e) {
            throw new BadRequestException(conflictMessage);
        }
    }

    public Card getCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(notFoundMessage));
    }

}
