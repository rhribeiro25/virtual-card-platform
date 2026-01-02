package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditImport {

    private UUID id;
    private String cardRef;
    private String providerCode;
    private UUID txRequestRef;
    private String rawPayload;
    private Boolean isProcessedCard;
    private Boolean isProcessedProvider;
    private Boolean isProcessedCardProvider;
    private Boolean isProcessedTransaction;
    private LocalDateTime createdAt;
}
