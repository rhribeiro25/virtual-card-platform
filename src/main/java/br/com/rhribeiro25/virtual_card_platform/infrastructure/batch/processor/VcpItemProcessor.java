package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processor;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardBrand;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.ProviderStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config.BatchCacheConfig;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto.VcpModelsGroup;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto.VirtualCardsCsvRow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@JobScope
@RequiredArgsConstructor
public class VcpItemProcessor implements ItemProcessor<VirtualCardsCsvRow, VcpModelsGroup> {

    private final BatchCacheConfig batchCacheConfig;

    @Override
    public VcpModelsGroup process(VirtualCardsCsvRow csvRow) {

        Transaction transaction = batchCacheConfig.transactionCache().get(csvRow.txRequestRef());
        if (transaction == null) {
            transaction = Transaction.builder()
                    .type(mapTransactionType(csvRow.txKind()))
                    .createdAt(LocalDateTime.now())
                    .amount(new BigDecimal(csvRow.txAmountTxt().replace(",", ".")))
                    .requestId(UUID.fromString(csvRow.txRequestRef()))
                    .build();
        }

        Card card = batchCacheConfig.cardCache().get(csvRow.cardRef());
        if (card == null) {
            card = Card.builder()
                    .externalId(csvRow.cardRef())
                    .createdAt(LocalDateTime.now())
                    .status(mapCardStatus(csvRow.state()))
                    .brand(mapBrand(csvRow.brandCode()))
                    .holderName(csvRow.holderNameRaw())
                    .balance(new BigDecimal(csvRow.balanceTxt().replace(",", ".")))
                    .internationalAllowed(mapBooleanAttribute(csvRow.internationalFlag()))
                    .expiryDate(parseExpiry(csvRow.expiryTxt()))
                    .cvv(Integer.parseInt(csvRow.cvvTxt()))
                    .pinCode(csvRow.pinTxt())
                    .maxDailyTransactions(Integer.parseInt(csvRow.maxDailyTxTxt()))
                    .maxTransactionAmount(new BigDecimal(csvRow.maxTxAmountTxt().replace(",", ".")))
                    .country(csvRow.issuingCountryCode())
                    .notes(csvRow.notesRaw())
                    .build();
        }

        Provider provider = batchCacheConfig.providerCache().get(csvRow.providerCode());
        if (provider == null) {
            provider = Provider.builder()
                    .code(csvRow.providerCode())
                    .createdAt(LocalDateTime.now())
                    .status(mapProviderStatus(csvRow.providerState()))
                    .country(csvRow.providerCountry())
                    .build();
        }

        CardProvider cardProvider = CardProvider.builder()
                .card(card)
                .provider(provider)
                .createdAt(LocalDateTime.now())
                .feePercentage(new BigDecimal(csvRow.providerFeePctTxt().replace(",", ".")))
                .dailyLimit(new BigDecimal(csvRow.providerDailyLimitTxt().replace(",", ".")))
                .priority(Integer.parseInt(csvRow.providerPriorityTxt()))
                .build();

        return VcpModelsGroup.builder()
                .card(card)
                .cardProvider(cardProvider)
                .provider(provider)
                .transaction(transaction)
                .build();
    }


    private CardBrand mapBrand(String brandCode) {

        return switch (brandCode) {
            case "01" -> CardBrand.VISA;
            case "02" -> CardBrand.MASTERCARD;
            case "03" -> CardBrand.AMEX;
            case "04" -> CardBrand.ELO;
            case "05" -> CardBrand.HIPERCARD;
            default -> throw new IllegalArgumentException(
                    "Unknown brand code: " + brandCode
            );
        };
    }


    private TransactionType mapTransactionType(String txKind) {

        return switch (txKind) {
            case "P" -> TransactionType.SPEND;
            case "C" -> TransactionType.TOPUP;
            case "T" -> TransactionType.TRANSFER;
            default -> throw new IllegalArgumentException(
                    "Invalid transaction type: " + txKind
            );
        };
    }

    private CardStatus mapCardStatus(String state) {

        return switch (state) {
            case "A" -> CardStatus.ACTIVE;
            case "B" -> CardStatus.BLOCKED;
            default -> throw new IllegalArgumentException(
                    "Invalid Card: " + state
            );
        };
    }

    private boolean mapBooleanAttribute(String bool) {

        return switch (bool) {
            case "Y" -> true;
            case "N" -> false;
            default -> throw new IllegalArgumentException(
                    "Invalid Card: " + bool
            );
        };
    }

    private static LocalDateTime parseExpiry(String expiryTxt) {
        if (expiryTxt == null || expiryTxt.isBlank()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth = YearMonth.parse(expiryTxt.trim(), formatter);

        return yearMonth
                .atEndOfMonth()
                .atTime(23, 59, 59);
    }

    private ProviderStatus mapProviderStatus(String state) {

        return switch (state) {
            case "A" -> ProviderStatus.ACTIVE;
            case "B" -> ProviderStatus.BLOCKED;
            default -> throw new IllegalArgumentException(
                    "Invalid Provider: " + state
            );
        };
    }

}