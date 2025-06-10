package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.Exception.NotFoundException;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    private final String CARD_SELECTED_NOT_FOUND_MESSAGE = "Selected card not found!\nPlease verify your card information and try again.";
    private final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance. Please verify your account and try again.";

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
        return cardRepository.save(card);
    }

    @Transactional
    public Card spend(UUID cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(CARD_SELECTED_NOT_FOUND_MESSAGE));

        transactionService.isDuplicateTransaction(card, amount);

        cardRepository.lockById(card.getId());

        if (card.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException(INSUFFICIENT_BALANCE_MESSAGE);
        }

        card.setBalance(card.getBalance().subtract(amount));
        cardRepository.save(card);

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
                .orElseThrow(() -> new NotFoundException(CARD_SELECTED_NOT_FOUND_MESSAGE));

        transactionService.isDuplicateTransaction(card, amount);

        cardRepository.lockById(card.getId());

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        transactionService.create(new Transaction.Builder()
                .card(card)
                .type(TransactionType.TOPUP)
                .amount(amount)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build());

        return card;
    }

    public Card getCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(CARD_SELECTED_NOT_FOUND_MESSAGE));
    }

}
