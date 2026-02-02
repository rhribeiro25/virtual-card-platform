package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.BatchAuditImportStatus;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
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
    private ObjectId id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Indexed
    private LocalDate actionFileDate;
    @Indexed
    private BatchAuditImportStatus status;

    private CsvFileRow csvFileRow;
    private UUID cardId;
    private UUID providerId;
    private UUID cardProviderId;
    private UUID transactionId;

    @Transient
    private Card card;

    @Transient
    private Provider provider;

    @Transient
    private CardProvider cardProvider;

    @Transient
    private Transaction transaction;

}
