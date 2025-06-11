package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.application.template.SpendTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.application.template.TopUpTransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.repository.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CardUsecase {

    private final CardRepository cardRepository;
    private final TransactionUsecase transactionUsecase;
    private final SpendTransactionProcessor spendProcessor;
    private final TopUpTransactionProcessor topUpProcessor;

    @Autowired
    public CardUsecase(CardRepository cardRepository,
                       TransactionUsecase transactionUsecase,
                       SpendTransactionProcessor spendProcessor,
                       TopUpTransactionProcessor topUpProcessor) {
        this.cardRepository = cardRepository;
        this.transactionUsecase = transactionUsecase;
        this.spendProcessor = spendProcessor;
        this.topUpProcessor = topUpProcessor;
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
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));
        return spendProcessor.process(card, amount);
    }

    @Transactional
    public Card topUp(UUID cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));
        return topUpProcessor.process(card, amount);
    }

    public Card getCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(MessageUtil.getMessage("card.notFound")));
    }

    public Page<Transaction> getTransactionsByValidCardId(UUID cardId, Pageable pageable) {
        getCardById(cardId);
        return transactionUsecase.getTransactionsByCardId(cardId, pageable);
    }

}
