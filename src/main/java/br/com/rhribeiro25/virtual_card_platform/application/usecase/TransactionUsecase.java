package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.TransactionRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionUsecase {

    private final TransactionRepository transactionRepository;

    public TransactionUsecase(TransactionRepository transactionRepository) {
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

        Optional<?> existingTransaction = transactionRepository.findDuplicateBetweenRangeTimeTransaction(
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

    public long countRecentSpends(UUID cardId, TransactionType transactionType) {
        int spendRecentMinutes = Integer.parseInt(MessageUtil.getMessage("card.spend.recent.minutes"));

        return transactionRepository.countRecentTransactions(
                cardId,
                transactionType,
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(spendRecentMinutes))
        );
    }

    public Page<Transaction> getTransactionsByCardId(UUID cardId, Pageable pageable) {
        return transactionRepository.findByCardId(cardId, pageable);
    }

    public Optional<Transaction> verifyDuplicateTransaction(UUID cardId, UUID requestId) {
        return transactionRepository.findByCardIdAndRequestId(cardId, requestId);
    }
}
