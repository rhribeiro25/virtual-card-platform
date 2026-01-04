package br.com.rhribeiro25.virtual_card_platform.domain.model;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a raw staging row loaded from a legacy CSV source.
 * <p>
 * This table is intentionally denormalized and append-only.
 * No business rules or unique constraints are enforced here.
 * <p>
 * Spring Batch Best Practice:
 * - No JPA
 * - JDBC only
 * - Chunk-oriented
 */
@Document(collection = "batch_audit_import")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchAuditImport {

    @Id
    private UUID id;
    private String cardRef;
    private String providerCode;
    private UUID txRequestRef;
    private CsvFileRow csvFileRow;
    private Boolean isProcessedCard;
    private Boolean isProcessedProvider;
    private Boolean isProcessedCardProvider;
    private Boolean isProcessedTransaction;
    private LocalDateTime createdAt;

    @Transient
    private Boolean isTransientEntitySaved;

    @Transient
    private Card card;

    @Transient
    private Provider provider;

    @Transient
    private CardProvider cardProvider;

    @Transient
    private Transaction transaction;

}
