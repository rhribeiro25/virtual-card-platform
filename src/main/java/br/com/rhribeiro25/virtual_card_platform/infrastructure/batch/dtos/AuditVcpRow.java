package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a raw staging row loaded from a legacy CSV source.
 *
 * This table is intentionally denormalized and append-only.
 * No business rules or unique constraints are enforced here.
 *
 * Spring Batch Best Practice:
 *  - No JPA
 *  - JDBC only
 *  - Chunk-oriented
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditVcpRow {

    /**
     * Technical identifier (batch-generated).
     */
    private UUID id;

    /**
     * External card reference from legacy system.
     * Example: "VC-00001"
     */
    private String cardRef;

    /**
     * External provider code.
     * Example: "ADYEN", "STRIPE"
     */
    private String providerCode;

    /**
     * External transaction request reference.
     * Used for idempotency in Transaction step.
     */
    private UUID txRequestRef;

    /**
     * Raw CSV row serialized as JSON.
     * Stored as JSONB in PostgreSQL.
     */
    private String rawPayload;

    /**
     * Indicates whether this row has already been processed
     * by downstream steps.
     */
    private Boolean processed;

    /**
     * Optional audit information.
     */
    private LocalDateTime createdAt;
}
