package br.com.rhribeiro25.virtual_card_platform.domain.model;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
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
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchAuditImport {

    @Id
    private UUID id;
    private String txRequestRef;
    private String cardRef;
    private String providerCode;
    private CsvFileRow csvFileRow;
    private LocalDateTime createdAt;
    private UUID persistedCardId;
    private UUID persistedProviderId;
    private UUID persistedCardProviderId;
    private UUID persistedTransactionId;

    @Transient
    private Card card;

    @Transient
    private Provider provider;

    @Transient
    private CardProvider cardProvider;

    @Transient
    private Transaction transaction;

}
