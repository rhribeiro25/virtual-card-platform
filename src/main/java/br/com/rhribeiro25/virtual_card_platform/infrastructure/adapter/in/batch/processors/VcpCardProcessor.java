package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardBrand;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.CardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component(SpringBatchProcessor.CARD)
@StepScope
@RequiredArgsConstructor
public class VcpCardProcessor implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    private final CardRepository cardRepository;

    @Override
    public BatchAuditImport process(BatchAuditImport batchAuditImport) throws JsonProcessingException {

        CsvFileRow csvFileRow = batchAuditImport.getCsvFileRow();
        if(cardRepository.existsByExternalId(csvFileRow.getCardRef())) return batchAuditImport;

        batchAuditImport.setCard(Card.builder()
                .externalId(csvFileRow.getCardRef())
                .createdAt(LocalDateTime.now())
                .status(mapCardStatus(csvFileRow.getState()))
                .brand(mapBrand(csvFileRow.getBrandCode()))
                .holderName(csvFileRow.getHolderNameRaw())
                .balance(new BigDecimal(csvFileRow.getBalanceTxt().replace(",", ".")))
                .internationalAllowed(mapBooleanAttribute(csvFileRow.getInternationalFlag()))
                .expiryDate(parseExpiry(csvFileRow.getExpiryTxt()))
                .cvv(Integer.parseInt(csvFileRow.getCvvTxt()))
                .pinCode(csvFileRow.getPinTxt())
                .maxDailyTransactions(Integer.parseInt(csvFileRow.getMaxDailyTxTxt()))
                .maxTransactionAmount(new BigDecimal(csvFileRow.getMaxTxAmountTxt().replace(",", ".")))
                .country(csvFileRow.getIssuingCountryCode())
                .notes(csvFileRow.getNotesRaw())
                .build());

        return batchAuditImport;
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