package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component(SpringBatchProcessor.CARD)
@StepScope
@RequiredArgsConstructor
public class VcpCardProcessor implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    private final CardUsecase cardUsecase;
    private final CardService cardService;
    private final BigDecimalUtils bigDecimalUtils;
    private final DateUtils dateUtils;

    @Override
    public BatchAuditImport process(BatchAuditImport batchAuditImport) throws JsonProcessingException {

        CsvFileRow csvFileRow = batchAuditImport.getCsvFileRow();
        if (cardUsecase.existsByExternalId(csvFileRow.getCardRef())) return batchAuditImport;

        batchAuditImport.setCard(Card.builder()
                .externalId(csvFileRow.getCardRef())
                .createdAt(LocalDateTime.now())
                .status(cardService.mapStatus(csvFileRow.getState()))
                .brand(cardService.mapBrand(csvFileRow.getBrandCode()))
                .holderName(csvFileRow.getHolderNameRaw())
                .balance(bigDecimalUtils.stringToBigDecimal(csvFileRow.getBalanceTxt()))
                .internationalAllowed(cardService.mapBooleanAttribute(csvFileRow.getInternationalFlag()))
                .expiryDate(dateUtils.MM_YY_TO_LocalDateTime(csvFileRow.getExpiryTxt()))
                .cvv(Integer.parseInt(csvFileRow.getCvvTxt()))
                .pinCode(csvFileRow.getPinTxt())
                .maxDailyTransactions(Integer.parseInt(csvFileRow.getMaxDailyTxTxt()))
                .maxTransactionAmount(bigDecimalUtils.stringToBigDecimal(csvFileRow.getMaxTxAmountTxt()))
                .country(csvFileRow.getIssuingCountryCode())
                .notes(csvFileRow.getNotesRaw())
                .build());

        return batchAuditImport;
    }



}