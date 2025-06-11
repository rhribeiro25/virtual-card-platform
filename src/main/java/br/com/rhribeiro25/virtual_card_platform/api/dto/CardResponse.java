package br.com.rhribeiro25.virtual_card_platform.api.dto;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record CardResponse(
        UUID id,
        String cardholderName,
        CardStatus status,
        BigDecimal balance,
        Timestamp createdAt
) {}
