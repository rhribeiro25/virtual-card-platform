package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single CSV row received from an external/legacy card provider system.
 * All fields are kept as String to preserve the original format and allow
 * explicit validation and normalization in the ItemProcessor.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CsvRow {
    // === Card identification (legacy system) ===
    private String cardRef;              // "VC-9981" (external card reference)
    private String state;                // "A" | "B" (A=Active, B=Blocked)
    private String brandCode;            // "01"=VISA | "02"=MASTERCARD | "03"=AMEX | "04"=ELO | "05"=HIPERCARD
    private String holderNameRaw;        // "JOHN  DOE"

    // === Card financial data (string-based formats) ===
    private String balanceTxt;           // "1.234,56"
    private String currencyCode;         // "BRL", "USD"
    private String internationalFlag;    // "Y" | "N"

    // === Security & lifecycle ===
    private String expiryTxt;            // "12/27"
    private String cvvTxt;               // "123"
    private String pinTxt;               // "4321"

    // === Limits (external representation) ===
    private String maxDailyTxTxt;         // "20"
    private String maxTxAmountTxt;        // "5.000,00"

    // === Country & metadata ===
    private String issuingCountryCode;   // "BR", "US"
    private String notesRaw;             // free text, legacy notes

    // === Provider data (flattened in the same row) ===
    private String providerCode;          // "STRIPE", "ADYEN"
    private String providerFeePctTxt;     // "2.75"
    private String providerDailyLimitTxt; // "10000"
    private String providerPriorityTxt;   // "1"
    private String providerState;         // "A" | "B" (A=Active, B=Blocked)
    private String providerCountry;       // "Brazil" "France" "Portugal"

    // === Transaction snapshot (optional, legacy) ===
    private String txKind;                // "P"=SPEND | "C"=TOPUP | "T"=TRANSFER
    private String txAmountTxt;           // "250,00"
    private String txRequestRef;           // external request reference
}