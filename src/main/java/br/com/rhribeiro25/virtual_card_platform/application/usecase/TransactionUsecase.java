package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.TransactionRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.BadRequestException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.InternalServerErrorException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.MergeUtils.mergeField;

@Service
public class TransactionUsecase {

    private final TransactionRepository transactionRepository;

    public TransactionUsecase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void isDuplicateTransaction(Card card, BigDecimal amount, TransactionType type) {
        int rangeTimeTransaction = Integer.parseInt(MessageUtils.getMessage("card.conflict"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rangeStart = now.minusMinutes(rangeTimeTransaction);

        Optional<?> existingTransaction = transactionRepository.findDuplicateBetweenRangeTimeTransaction(amount, card.getId(), rangeStart, now, type);

        if (existingTransaction.isPresent()) {
            String duplicateTransactionMessage = MessageUtils.getMessage("card.conflict");
            throw new BadRequestException(duplicateTransactionMessage);
        }
    }

    public long countRecentSpends(UUID cardId, TransactionType transactionType) {
        int spendRecentMinutes = Integer.parseInt(MessageUtils.getMessage("card.spend.recent.minutes"));

        LocalDateTime from = LocalDateTime.now().minusMinutes(spendRecentMinutes);
        return transactionRepository.countRecentTransactions(cardId, transactionType, from);
    }

    public Page<Transaction> getTransactionsByCardId(UUID cardId, Pageable pageable) {
        return transactionRepository.findByCardId(cardId, pageable);
    }

    public Optional<Transaction> verifyDuplicateTransaction(UUID cardId, String requestId) {
        return transactionRepository.findByCardIdAndRequestId(cardId, requestId);
    }

    /*******************************************************************************************************************
     SPRING BATCH METHODS
     ********************************************************************************************************************/
    public Transaction saveByBatch(Transaction transaction) {
        try {
            return transactionRepository.save(transaction);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new ConflictException(MessageUtils.getMessage("transaction.conflict"));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    public boolean existsByRequestId(String requestId) {
        return transactionRepository.existsByRequestId(requestId);
    }

    public Optional<UUID> findIdByRequestId(String requestId) {
        return transactionRepository.findIdByRequestId(requestId);
    }

    public Optional<Transaction> findByRequestId(String requestId) {
        return transactionRepository.findByRequestId(requestId);
    }

    public void merge(Transaction existing, Transaction incoming) {
        if (existing == null || incoming == null) return;
        mergeField(existing.getType(), incoming.getType(), existing::setType);
        mergeField(existing.getAmount(), incoming.getAmount(), existing::setAmount);
        mergeField(existing.getRequestId(), incoming.getRequestId(), existing::setRequestId);
    }


}
