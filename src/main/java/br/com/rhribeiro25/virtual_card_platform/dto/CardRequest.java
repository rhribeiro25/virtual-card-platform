package br.com.rhribeiro25.virtual_card_platform.dto;

import java.math.BigDecimal;

public record CardRequest(String cardholderName, BigDecimal initialBalance) {}
