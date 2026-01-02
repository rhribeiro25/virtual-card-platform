package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardBrand;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.CsvRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.JobScopeCacheUtils;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.StepScopeCacheUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpCardProcessor implements ItemProcessor<AuditImport, Card> {

    private final JobScopeCacheUtils jobScopeCacheUtils;
    private final StepScopeCacheUtils stepScopeCacheUtils;
    private final ObjectMapper objectMapper;

    @Override
    public Card process(AuditImport auditImport) throws JsonProcessingException {

        CsvRow csvRow = objectMapper.readValue(auditImport.getRawPayload(), CsvRow.class);
        Card card = jobScopeCacheUtils.cardCache().get(csvRow.getCardRef());
        if (card == null) {
            card = Card.builder()
                    .externalId(csvRow.getCardRef())
                    .createdAt(LocalDateTime.now())
                    .status(mapCardStatus(csvRow.getState()))
                    .brand(mapBrand(csvRow.getBrandCode()))
                    .holderName(csvRow.getHolderNameRaw())
                    .balance(new BigDecimal(csvRow.getBalanceTxt().replace(",", ".")))
                    .internationalAllowed(mapBooleanAttribute(csvRow.getInternationalFlag()))
                    .expiryDate(parseExpiry(csvRow.getExpiryTxt()))
                    .cvv(Integer.parseInt(csvRow.getCvvTxt()))
                    .pinCode(csvRow.getPinTxt())
                    .maxDailyTransactions(Integer.parseInt(csvRow.getMaxDailyTxTxt()))
                    .maxTransactionAmount(new BigDecimal(csvRow.getMaxTxAmountTxt().replace(",", ".")))
                    .country(csvRow.getIssuingCountryCode())
                    .notes(csvRow.getNotesRaw())
                    .build();
        }

        stepScopeCacheUtils.auditCache().put(auditImport.getId().toString(), auditImport);

        return card;

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

}