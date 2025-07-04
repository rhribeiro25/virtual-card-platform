package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.application.template.SpendTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.application.template.TopUpTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.TransactionRequest;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BusinessException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.InternalServerErrorException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.shared.mapper.TransactionMapper;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Caching(evict = {
            @CacheEvict(cacheNames = "cardsById", key = "#card.id")
    })
    @Transactional(rollbackFor = BusinessException.class)
    public Card create(Card card) {
        try {
            return cardRepository.save(card);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new ConflictException(MessageUtils.getMessage("card.conflict"));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "transactionsByCardId", key = "#cardId"),
    })
    @Transactional(rollbackFor = BusinessException.class)
    public Card spend(UUID cardId, TransactionRequest transactionRequest) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException(MessageUtils.getMessage("card.notFound")));
        Transaction transaction = TransactionMapper.toEntity(transactionRequest, card, TransactionType.SPEND);
        return spendProcessor.process(transaction);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "transactionsByCardId", key = "#cardId"),
    })
    @Transactional(rollbackFor = BusinessException.class)
    public Card topUp(UUID cardId, TransactionRequest transactionRequest) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException(MessageUtils.getMessage("card.notFound")));
        Transaction transaction = TransactionMapper.toEntity(transactionRequest, card, TransactionType.TOPUP);
        return topUpProcessor.process(transaction);
    }

    @Cacheable(value = "cardsById", key = "#cardId")
    public Card getCardById(UUID cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException(MessageUtils.getMessage("card.notFound")));
    }

    @Cacheable(value = "transactionsByCardId", key = "#cardId")
    public Page<Transaction> getTransactionsByValidCardId(UUID cardId, Pageable pageable) {
        getCardById(cardId);
        return transactionUsecase.getTransactionsByCardId(cardId, pageable);
    }

}
