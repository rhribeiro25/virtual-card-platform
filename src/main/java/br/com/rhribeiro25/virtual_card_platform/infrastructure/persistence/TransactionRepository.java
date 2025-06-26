package br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.amount = :amount
      AND t.card.id = :cardId
      AND t.createdAt BETWEEN :start AND :end
      AND t.type = :type""")
    Optional<Transaction> findDuplicateBetweenRangeTimeTransaction(
            @Param("amount") BigDecimal amount,
            @Param("cardId") UUID cardId,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("type") TransactionType type
    );

    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.card.id = :cardId " +
            "AND t.type = :type " +
            "AND t.createdAt >= :since")
    long countRecentTransactions(@Param("cardId") UUID cardId,
                           @Param("type") TransactionType type,
                           @Param("since") Timestamp since);

    Page<Transaction> findByCardId(UUID cardId, Pageable pageable);

    Optional<Transaction> findByCardIdAndRequestId(UUID cardId, UUID requestId);
}
