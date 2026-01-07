package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.*;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component(SpringBatchProcessor.CARD_PROVIDER)
@StepScope
@SuperBuilder
public class VcpCardProviderProcessor extends VcpAbstractBatchProcessor<CardProvider> {

    private final CardUsecase cardUsecase;
    private final ProviderUsecase providerUsecase;
    private final CardProviderUsecase cardProviderUsecase;
    private final BigDecimalUtils bigDecimalUtils;

    @Override
    protected boolean shouldSkip(CsvFileRow row, BatchAuditImport item) {
        return cardProviderUsecase.existsByCardAndProvider(
                cardUsecase.getCardByExternalId(item.getCardRef()).orElse(null),
                providerUsecase.getProviderByCode(item.getProviderCode()).orElse(null)
        );
    }

    @Override
    protected boolean dependenciesResolved(BatchAuditImport item) {
        return cardUsecase.getCardByExternalId(item.getCardRef()).isPresent()
                && providerUsecase.getProviderByCode(item.getProviderCode()).isPresent();
    }

    @Override
    protected CardProvider buildEntity(CsvFileRow row, BatchAuditImport item) {
        Card card = cardUsecase.getCardByExternalId(item.getCardRef()).orElseThrow();
        Provider provider = providerUsecase.getProviderByCode(item.getProviderCode()).orElseThrow();

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
