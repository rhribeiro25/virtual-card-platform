package br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CardUpdateStrategy implements ActionTypeStrategy<Card, BatchAuditImport> {

    private final CardService cardService;
    private final BigDecimalUtils bigDecimalUtils;

    @Override
    public boolean isSupportedActionType(ActionType type) {
        return type.equals(ActionType.UPC);
    }

    @Override
    public Class<Card> isSupportedModelType() {
        return Card.class;
    }

    @Override
    public Card execute(BatchAuditImport item) {

        CsvFileRow row = item.getCsvFileRow();

        Card card = new Card();

        card.setExternalId(item.getCardRef());

        if (StringUtils.hasText(row.getState()))
            card.setStatus(cardService.mapStatus(row.getState()));

        if (StringUtils.hasText(row.getBalanceTxt()))
            card.setBalance(bigDecimalUtils.stringToBigDecimal(row.getBalanceTxt()));

        if (StringUtils.hasText(row.getInternationalFlag()))
            card.setInternationalAllowed(cardService.mapBooleanAttribute(row.getInternationalFlag()));

        if (StringUtils.hasText(row.getMaxTxAmountTxt()))
            card.setMaxTransactionAmount(bigDecimalUtils.stringToBigDecimal(row.getMaxTxAmountTxt()));

        if (StringUtils.hasText(row.getMaxDailyTxTxt()))
            card.setMaxDailyTransactions(Integer.parseInt(row.getMaxDailyTxTxt()));

        if (StringUtils.hasText(row.getNotesRaw()))
            card.setNotes(row.getNotesRaw().trim());

        if (StringUtils.hasText(row.getCvvTxt()))
            card.setCvv(Integer.parseInt(row.getCvvTxt()));

        if (StringUtils.hasText(row.getPinTxt()))
            card.setPinCode(row.getPinTxt());

        if (StringUtils.hasText(row.getMaxDailyTxTxt()))
            card.setMaxDailyTransactions(Integer.parseInt(row.getMaxDailyTxTxt()));

        return card;
    }
}
