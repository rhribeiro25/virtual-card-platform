package br.com.rhribeiro25.virtual_card_platform.application.dto;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CardResponse(
        UUID id,
        String cardholderName,
        CardStatus status,
        BigDecimal balance,
        LocalDateTime createdAt
) {}
