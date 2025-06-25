package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CardRequest(
        @NotBlank(message = "{cardholderName.notBlank}")
        String cardholderName,

        @NotNull(message = "{initialBalance.notNull}")
        @DecimalMin(value = "0.0", inclusive = true, message = "{initialBalance.decimalMin}")
        BigDecimal initialBalance
) {}
