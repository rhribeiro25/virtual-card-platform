package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto;

import lombok.Builder;

/**
 * Represents a single CSV row received from an external/legacy card provider system.
 * All fields are kept as String to preserve the original format and allow
 * explicit validation and normalization in the ItemProcessor.
 */
@Builder
public record ExternalCardCsvRow(

        // === Card identification (legacy system) ===
        String cardRef,              // "VC-9981" (external card reference)
        String state,                // "A" | "B" (A=Active, B=Blocked)
        String brandCode,            // "01"=VISA | "02"=MASTERCARD | "03"=AMEX | "04"=ELO | "05"=HIPERCARD
        String holderNameRaw,        // "JOHN  DOE"

        // === Card financial data (string-based formats) ===
        String balanceTxt,           // "1.234,56"
        String currencyCode,         // "BRL", "USD"
        String activeFlag,           // "Y" | "N"
        String internationalFlag,    // "Y" | "N"

        // === Security & lifecycle ===
        String expiryTxt,            // "12/27"
        String cvvTxt,               // "123"
        String pinTxt,               // "4321"

        // === Limits (external representation) ===
        String maxDailyTxTxt,         // "20"
        String maxTxAmountTxt,        // "5.000,00"

        // === Country & metadata ===
        String issuingCountryCode,   // "BR", "US"
        String notesRaw,             // free text, legacy notes

        // === Provider data (flattened in the same row) ===
        String providerCode,          // "STRIPE", "ADYEN"
        String providerFeePctTxt,     // "2.75"
        String providerDailyLimitTxt, // "10000"
        String providerPriorityTxt,   // "1"
        String providerEnabledFlag,   // "Y" | "N"

        // === Transaction snapshot (optional, legacy) ===
        String txKind,                // "P"=SPEND | "C"=TOPUP | "T"=TRANSFER
        String txAmountTxt,           // "250,00"
        String txRequestRef           // external request reference
) {}
