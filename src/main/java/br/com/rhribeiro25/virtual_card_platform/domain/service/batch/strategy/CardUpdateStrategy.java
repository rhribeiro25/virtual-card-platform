package br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.DateUtils.MM_YY_ToLocalDateTime;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.cardMap;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.StringUtils.*;

@Service
@RequiredArgsConstructor
public class CardUpdateStrategy implements ActionTypeStrategy<Card, BatchAuditImport> {

    private final CardUsecase cardUsecase;
    private final CardService cardService;
    private final BigDecimalUtils bigDecimalUtils;

    @Override
    public boolean isSupportedActionType(ActionType type) {
        return type.equals(ActionType.UPC) || type.equals(ActionType.UPT);
    }

    @Override
    public Class<Card> isSupportedModelType() {
        return Card.class;
    }

    @Override
    public Card execute(BatchAuditImport item) {

        CsvFileRow row = item.getCsvFileRow();
        Optional<Card> optionalCard = cardUsecase.findByExternalId(row.getCardRef());
        Card card = optionalCard.orElseGet(() -> cardMap.get(row.getCardRef()));

        String state = row.getState();
        String brandCode = row.getBrandCode();
        String holderNameRaw = row.getHolderNameRaw();
        String balanceTxt = row.getBalanceTxt();
        String internationalFlag = row.getInternationalFlag();
        String expiryTxt = row.getExpiryTxt();
        String cvvTxt = row.getCvvTxt();
        String pinTxt = row.getPinTxt();
        String maxDailyTxTxt = row.getMaxDailyTxTxt();
        String maxTxAmountTxt = row.getMaxTxAmountTxt();
        String issuingCountryCode = row.getIssuingCountryCode();
        String notesRaw = row.getNotesRaw();

        updateIfChanged(
                card.getStatus(),
                whenHasText(state, () -> cardService.mapStatus(state)),
                card::setStatus
        );
        updateIfChanged(
                card.getBrand(),
                whenHasText(brandCode, () -> cardService.mapBrand(brandCode)),
                card::setBrand
        );
        updateIfHasText(card.getHolderName(), holderNameRaw, card::setHolderName);
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
                card.getExpiryDate(),
                whenHasText(expiryTxt, () -> MM_YY_ToLocalDateTime(expiryTxt.trim())),
                card::setExpiryDate
        );
        updateIfChanged(
                card.getCvv(),
                whenHasText(cvvTxt, () -> Integer.parseInt(cvvTxt.trim())),
                card::setCvv
        );
        updateIfHasText(card.getPinCode(), pinTxt, card::setPinCode);
        updateIfChanged(
                card.getMaxDailyTransactions(),
                whenHasText(maxDailyTxTxt, () -> Integer.parseInt(maxDailyTxTxt.trim())),
                card::setMaxDailyTransactions
        );
        updateIfChanged(
                card.getMaxTransactionAmount(),
                whenHasText(maxTxAmountTxt, () -> bigDecimalUtils.stringToBigDecimal(maxTxAmountTxt.trim())),
                card::setMaxTransactionAmount
        );
        updateIfHasText(card.getCountry(), issuingCountryCode, card::setCountry);
        updateIfHasText(card.getNotes(), notesRaw, card::setNotes);

        cardMap.put(row.getCardRef(), card);

        return card;
    }

}
