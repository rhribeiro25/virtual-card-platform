package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.application.dto.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.application.dto.CsvRow;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.JobScopeCacheUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.StepScopeCacheUtils;
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

    private final JobScopeCacheUtils jobScopeCacheUtils;
    private final StepScopeCacheUtils stepScopeCacheUtils;
    private final ObjectMapper objectMapper;

    @Override
    public CardProvider process(AuditImport auditImport) throws JsonProcessingException {

        CsvRow csvRow = objectMapper.readValue(auditImport.getRawPayload(), CsvRow.class);

        String keyMap = csvRow.getCardRef() + "_" + csvRow.getProviderCode();
        CardProvider cardProvider = jobScopeCacheUtils.cardProviderCache().get(keyMap);
        if (cardProvider == null) {
            Card card = jobScopeCacheUtils.cardCache().get(csvRow.getCardRef());
            Provider provider = jobScopeCacheUtils.providerCache().get(csvRow.getProviderCode());
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
        stepScopeCacheUtils.auditCache().put(auditImport.getId().toString(), auditImport);
        return cardProvider;
    }
}