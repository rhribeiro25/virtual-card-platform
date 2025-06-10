package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Value("${card.duplicateTransaction}")
    private String duplicateTransactionMessage;

    @Value("${card.transactionRangeMinutes}")
    private int rangeTimeTransaction;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public void isDuplicateTransaction(Card card, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesAgo = now.minusMinutes(rangeTimeTransaction);

        Optional<?> existingTransaction = transactionRepository.findDuplicateTransaction(
                amount,
                card.getId(),
                tenMinutesAgo,
                now
        );

        if (existingTransaction.isPresent()) {
            throw new BadRequestException(duplicateTransactionMessage);
        }
    }

    public long countRecentSpends(UUID cardId) {
        return transactionRepository.countRecentSpends(cardId);
    }

    public Page<Transaction> getTransactionsByCardId(UUID cardId, Pageable pageable) {
        return transactionRepository.findByCardId(cardId, pageable);
    }
}
