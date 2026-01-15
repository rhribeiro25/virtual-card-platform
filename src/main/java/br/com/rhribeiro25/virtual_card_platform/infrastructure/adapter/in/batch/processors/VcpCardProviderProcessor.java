package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.*;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component(SpringBatchProcessor.CARD_PROVIDER)
@StepScope
@RequiredArgsConstructor
public class VcpCardProviderProcessor extends VcpAbstractBatchProcessor<CardProvider> {

    private final CardUsecase cardUsecase;
    private final ProviderUsecase providerUsecase;
    private final BigDecimalUtils bigDecimalUtils;

    @Override
    protected boolean dependenciesResolved(BatchAuditImport item) {
        return cardUsecase.getCardByExternalId(item.getCsvFileRow().getCardRef()).isPresent()
                && providerUsecase.getProviderByCode(item.getCsvFileRow().getProviderCode()).isPresent();
    }

    @Override
    protected CardProvider buildEntity(ActionType actionType, BatchAuditImport item) {
        CsvFileRow row = item.getCsvFileRow();
        Card card = cardUsecase.getCardByExternalId(item.getCsvFileRow().getCardRef()).orElseThrow();
        Provider provider = providerUsecase.getProviderByCode(item.getCsvFileRow().getProviderCode()).orElseThrow();

        return CardProvider.builder()
                .createdAt(LocalDateTime.now())
                .feePercentage(bigDecimalUtils.stringToBigDecimal(row.getProviderFeePctTxt()))
                .dailyLimit(bigDecimalUtils.stringToBigDecimal(row.getProviderDailyLimitTxt()))
                .priority(Integer.parseInt(row.getProviderPriorityTxt()))
                .card(card)
                .provider(provider)
                .build();
    }

    @Override
    protected void attachEntity(BatchAuditImport item, CardProvider entity) {
        item.setCardProvider(entity);
    }

}
