package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.repository.TransactionRepository;
import br.com.rhribeiro25.virtual_card_platform.utils.MessageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public void isDuplicateTransaction(Card card, BigDecimal amount, TransactionType type) {
        int rangeTimeTransaction = Integer.parseInt(MessageUtil.getMessage("card.transactionRangeMinutes"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rangeStart = now.minusMinutes(rangeTimeTransaction);

        Timestamp start = Timestamp.valueOf(rangeStart);
        Timestamp end = Timestamp.valueOf(now);

        Optional<?> existingTransaction = transactionRepository.findDuplicateTransaction(
                amount,
                card.getId(),
                start,
                end,
                type
        );

        if (existingTransaction.isPresent()) {
            String duplicateTransactionMessage = MessageUtil.getMessage("card.duplicateTransaction");
            throw new BadRequestException(duplicateTransactionMessage);
        }
    }

    public long countRecentSpends(UUID cardId) {
        int spendRecentMinutes = Integer.parseInt(MessageUtil.getMessage("card.spend.recent.minutes"));

        return transactionRepository.countRecentSpends(
                cardId,
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(spendRecentMinutes))
        );
    }

    public Page<Transaction> getTransactionsByCardId(UUID cardId, Pageable pageable) {
        return transactionRepository.findByCardId(cardId, pageable);
    }
}
