package br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateCardStrategy implements ActionTypeStrategy<Card, BatchAuditImport> {

    private final CardService cardService;
    private final BigDecimalUtils bigDecimalUtils;
    private final DateUtils dateUtils;

    @Override
    public boolean isSupportedActionType(ActionType type) {
        return type.equals(ActionType.NEW);
    }

    @Override
    public Class<Card> isSupportedModelType() {
        return Card.class;
    }

    @Override
    public Card execute(BatchAuditImport item) {
        CsvFileRow row = item.getCsvFileRow();
        return Card.builder()
                .externalId(item.getCardRef())
                .createdAt(LocalDateTime.now())
                .status(cardService.mapStatus(row.getState()))
                .brand(cardService.mapBrand(row.getBrandCode()))
                .holderName(row.getHolderNameRaw())
                .balance(bigDecimalUtils.stringToBigDecimal(row.getBalanceTxt()))
                .internationalAllowed(cardService.mapBooleanAttribute(row.getInternationalFlag()))
                .expiryDate(dateUtils.MM_YY_TO_LocalDateTime(row.getExpiryTxt()))
                .cvv(Integer.parseInt(row.getCvvTxt()))
                .pinCode(row.getPinTxt())
                .maxDailyTransactions(Integer.parseInt(row.getMaxDailyTxTxt()))
                .maxTransactionAmount(bigDecimalUtils.stringToBigDecimal(row.getMaxTxAmountTxt()))
                .country(row.getIssuingCountryCode())
                .notes(row.getNotesRaw())
                .build();
    }
}
