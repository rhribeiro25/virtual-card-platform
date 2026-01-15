package br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.StringUtils.*;

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

        Card card = new Card();

        CsvFileRow row = item.getCsvFileRow();
        String state = row.getState();
        String balanceTxt = row.getBalanceTxt();
        String internationalFlag = row.getInternationalFlag();
        String maxTxAmountTxt = row.getMaxTxAmountTxt();
        String maxDailyTxTxt = row.getMaxDailyTxTxt();
        String notesRaw = row.getNotesRaw();
        String cvvTxt = row.getCvvTxt();
        String pinTxt = row.getPinTxt();

        card.setExternalId(item.getCsvFileRow().getCardRef());

        updateIfChanged(
                card.getStatus(),
                whenHasText(state, () -> cardService.mapStatus(state)),
                card::setStatus
        );
        updateIfChanged(
                card.getBalance(),
                whenHasText(balanceTxt, () -> bigDecimalUtils.stringToBigDecimal(balanceTxt.trim())),
                card::setBalance
        );
        updateIfChanged(
                card.getInternationalAllowed(),
                whenHasText(internationalFlag, () -> cardService.mapBooleanAttribute(internationalFlag)),
                card::setInternationalAllowed
        );
        updateIfChanged(
                card.getMaxTransactionAmount(),
                whenHasText(maxTxAmountTxt, () -> bigDecimalUtils.stringToBigDecimal(maxTxAmountTxt)),
                card::setMaxTransactionAmount
        );
        updateIfChanged(
                card.getMaxDailyTransactions(),
                whenHasText(maxDailyTxTxt, () -> Integer.parseInt(maxDailyTxTxt.trim())),
                card::setMaxDailyTransactions
        );
        updateIfChanged(
                card.getCvv(),
                whenHasText(cvvTxt, () -> Integer.parseInt(cvvTxt.trim())),
                card::setCvv
        );
        updateIfHasText(card.getPinCode(), pinTxt, card::setPinCode);
        updateIfHasText(card.getNotes(), notesRaw, card::setNotes);
        return card;
    }
}
