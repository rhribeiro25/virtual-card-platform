package br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.DateUtils.MM_YY_ToLocalDateTime;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.cardMap;

@Service
@RequiredArgsConstructor
public class CardCreateStrategy implements ActionTypeStrategy<Card, BatchAuditImport> {

    private final CardService cardService;
    private final BigDecimalUtils bigDecimalUtils;

    @Override
    public boolean isSupportedActionType(ActionType type) {
        return type.equals(ActionType.CRC);
    }

    @Override
    public Class<Card> isSupportedModelType() {
        return Card.class;
    }

    @Override
    public Card execute(BatchAuditImport item) {
        CsvFileRow row = item.getCsvFileRow();
        Card card = Card.builder()
                .externalId(item.getCsvFileRow().getCardRef())
                .status(cardService.mapStatus(row.getState()))
                .brand(cardService.mapBrand(row.getBrandCode()))
                .holderName(row.getHolderNameRaw())
                .balance(bigDecimalUtils.stringToBigDecimal(row.getBalanceTxt()))
                .internationalAllowed(cardService.mapBooleanAttribute(row.getInternationalFlag()))
                .expiryDate(MM_YY_ToLocalDateTime(row.getExpiryTxt()))
                .cvv(Integer.parseInt(row.getCvvTxt()))
                .pinCode(row.getPinTxt())
                .maxDailyTransactions(Integer.parseInt(row.getMaxDailyTxTxt()))
                .maxTransactionAmount(bigDecimalUtils.stringToBigDecimal(row.getMaxTxAmountTxt()))
                .country(row.getIssuingCountryCode())
                .notes(row.getNotesRaw())
                .build();

        cardMap.put(row.getCardRef(), card);

        return card;
    }
}
