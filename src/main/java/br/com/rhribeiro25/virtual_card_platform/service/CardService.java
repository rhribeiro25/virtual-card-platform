package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.repository.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public CardService(CardRepository cardRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Card createCard(String cardholderName, BigDecimal initialBalance) {
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
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // Bloqueia a entidade no banco para evitar race condition
        cardRepository.lockById(card.getId());

        if (card.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        card.setBalance(card.getBalance().subtract(amount));
        cardRepository.save(card);

        transactionRepository.save(new Transaction.Builder()
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
                .orElseThrow(() -> new RuntimeException("Card not found"));

        cardRepository.lockById(card.getId());

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        transactionRepository.save(new Transaction.Builder()
                .card(card)
                .type(TransactionType.TOPUP)
                .amount(amount)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build());

        return card;
    }

    public Card getCard(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

}
