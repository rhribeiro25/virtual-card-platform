package br.com.rhribeiro25.virtual_card_platform.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CardRequest(
        @NotBlank(message = "{cardholderName.notBlank}")
        String cardholderName,

        @NotNull(message = "{initialBalance.notNull}")
        @DecimalMin(value = "0.0", inclusive = true, message = "{initialBalance.decimalMin}")
        BigDecimal initialBalance,

        Boolean isActive,
        Boolean isInternationalAllowed,
        LocalDateTime createdAt

        ) {
}
