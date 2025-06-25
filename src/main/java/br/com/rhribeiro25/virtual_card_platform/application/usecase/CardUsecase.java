package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.application.template.SpendTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.application.template.TopUpTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.OptimisticLockException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CardUsecase {

    private final CardRepository cardRepository;
    private final TransactionUsecase transactionUsecase;
    private final SpendTransactionProcessor spendProcessor;
    private final TopUpTransactionProcessor topUpProcessor;

    @Autowired
    public CardUsecase(CardRepository cardRepository, TransactionUsecase transactionUsecase, SpendTransactionProcessor spendProcessor, TopUpTransactionProcessor topUpProcessor) {
        this.cardRepository = cardRepository;
        this.transactionUsecase = transactionUsecase;
        this.spendProcessor = spendProcessor;
        this.topUpProcessor = topUpProcessor;
    }

    @Caching(evict = {@CacheEvict(cacheNames = "TransactionsByValidCardId", allEntries = true), @CacheEvict(cacheNames = "CardById", allEntries = true)})
    @Transactional
    public Card create(Card card) {
        try {
            return cardRepository.save(card);
        } catch (Exception e) {
            throw new OptimisticLockException(MessageUtil.getMessage("card.conflict"));
        }
    }

    @Caching(evict = {@CacheEvict(cacheNames = "TransactionsByValidCardId", allEntries = true), @CacheEvict(cacheNames = "CardById", key = "#cardId")})
    @Transactional
    public Card spend(UUID cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));
        return spendProcessor.process(card, amount);
    }

    @Caching(evict = {@CacheEvict(cacheNames = "TransactionsByValidCardId", allEntries = true), @CacheEvict(cacheNames = "CardById", key = "#cardId")})
    @Transactional
    public Card topUp(UUID cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));
        return topUpProcessor.process(card, amount);
    }

    @Cacheable(value = "CardById", key = "#cardId")
    public Card getCardById(UUID cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));
    }

    @Cacheable(value = "TransactionsByValidCardId", key = "#cardId ?: 'all'")
    public Page<Transaction> getTransactionsByValidCardId(UUID cardId, Pageable pageable) {
        getCardById(cardId);
        return transactionUsecase.getTransactionsByCardId(cardId, pageable);
    }

}
