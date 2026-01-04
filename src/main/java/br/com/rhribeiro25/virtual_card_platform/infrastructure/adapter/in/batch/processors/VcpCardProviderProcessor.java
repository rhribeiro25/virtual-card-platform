package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.ProviderUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.*;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VcpCardProviderProcessor implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    private final CardUsecase cardUsecase;
    private final ProviderUsecase providerUsecase;
    private final CardProviderUsecase cardProviderUsecase;

    @Override
    public BatchAuditImport process(BatchAuditImport batchAuditImport) throws JsonProcessingException {

        CsvFileRow csvFileRow = batchAuditImport.getCsvFileRow();
        Optional<Card> card = cardUsecase.getCardByExternalId(batchAuditImport.getCardRef());
        Optional<Provider> provider = providerUsecase.getProviderByCode(batchAuditImport.getProviderCode());

        if (card.isEmpty() || provider.isEmpty()) return null;
        if (cardProviderUsecase.existsByCardAndProvider(card.get(), provider.get())) return batchAuditImport;

        batchAuditImport.setCardProvider(CardProvider.builder()
                .createdAt(LocalDateTime.now())
                .feePercentage(new BigDecimal(csvFileRow.getProviderFeePctTxt().replace(",", ".")))
                .dailyLimit(new BigDecimal(csvFileRow.getProviderDailyLimitTxt().replace(",", ".")))
                .priority(Integer.parseInt(csvFileRow.getProviderPriorityTxt()))
                .card(card.get())
                .provider(provider.get())
                .build());

        return batchAuditImport;
    }

}
