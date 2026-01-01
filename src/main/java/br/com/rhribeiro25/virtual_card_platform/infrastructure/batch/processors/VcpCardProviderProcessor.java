package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.CsvRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpCardProviderProcessor implements ItemProcessor<AuditImport, CardProvider> {

    private final BatchCacheUtils batchCacheUtils;
    private final ObjectMapper objectMapper;

    @Override
    public CardProvider process(AuditImport auditImport) throws JsonProcessingException {

        CsvRow csvRow = objectMapper.readValue(auditImport.getRawPayload(), CsvRow.class);

        String keyMap = csvRow.getCardRef() + "_" + csvRow.getProviderCode();
        CardProvider cardProvider = batchCacheUtils.cardProviderCache().get(keyMap);
        if (cardProvider == null) {
            Card card = batchCacheUtils.cardCache().get(csvRow.getCardRef());
            Provider provider = batchCacheUtils.providerCache().get(csvRow.getProviderCode());
            cardProvider = CardProvider.builder()
                    .card(card)
                    .provider(provider)
                    .createdAt(LocalDateTime.now())
                    .feePercentage(new BigDecimal(csvRow.getProviderFeePctTxt().replace(",", ".")))
                    .dailyLimit(new BigDecimal(csvRow.getProviderDailyLimitTxt().replace(",", ".")))
                    .priority(Integer.parseInt(csvRow.getProviderPriorityTxt()))
                    .keyMap(keyMap)
                    .build();
        }
        batchCacheUtils.auditCache().put(auditImport.getId().toString(), auditImport);
        return cardProvider;
    }
}