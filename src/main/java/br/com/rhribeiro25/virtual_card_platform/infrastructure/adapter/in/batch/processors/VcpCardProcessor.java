package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component(SpringBatchProcessor.CARD)
@StepScope
@RequiredArgsConstructor
public class VcpCardProcessor extends VcpAbstractBatchProcessor<Card> {

    private final CardUsecase cardUsecase;
    private final CardService cardService;
    private final BigDecimalUtils bigDecimalUtils;
    private final DateUtils dateUtils;


    @Override
    protected boolean shouldSkip(CsvFileRow row, BatchAuditImport item) {
        return cardUsecase.existsByExternalId(row.getCardRef());
    }

    @Override
    protected boolean dependenciesResolved(BatchAuditImport item) {
        return true;
    }

    @Override
    protected Card buildEntity(CsvFileRow row, BatchAuditImport item) {
        return Card.builder()
                .externalId(row.getCardRef())
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

    @Override
    protected void attachEntity(BatchAuditImport item, Card card) {
        item.setCard(card);
    }

}