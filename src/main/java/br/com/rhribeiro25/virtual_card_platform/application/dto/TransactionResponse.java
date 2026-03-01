package br.com.rhribeiro25.virtual_card_platform.application.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID cardId,
        String type,
        BigDecimal amount,
        LocalDateTime createdAt
) {}

