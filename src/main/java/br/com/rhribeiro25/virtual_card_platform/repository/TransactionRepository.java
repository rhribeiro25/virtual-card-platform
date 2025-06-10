package br.com.rhribeiro25.virtual_card_platform.repository;

import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
        SELECT t FROM Transaction t\s
        WHERE t.amount = :amount\s
          AND t.cardId = :cardId
          AND t.timestamp BETWEEN :startTime AND :endTime
   \s""")
    Optional<Transaction> findDuplicateTransaction(
            @Param("amount") BigDecimal amount,
            @Param("cardId") UUID cardId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
    SELECT COUNT(t) FROM Transaction t
    WHERE t.card.id = :cardId
      AND t.type = 'SPEND'
      AND t.createdAt >= CURRENT_TIMESTAMP - INTERVAL '1 minute'
""")
    long countRecentSpends(@Param("cardId") UUID cardId);

    Page<Transaction> findByCardId(UUID cardId, Pageable pageable);
}
