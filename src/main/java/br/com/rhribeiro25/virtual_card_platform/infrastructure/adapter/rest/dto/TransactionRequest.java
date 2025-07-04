package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
        @NotNull(message = "{transaction.amount.notNull}")
        @DecimalMin(value = "0.01", inclusive = true, message = "{transaction.amount.decimalMin}")
        BigDecimal amount,

        @NotNull(message = "{transaction.requestId.notNull}")
        UUID requestId
) {}


