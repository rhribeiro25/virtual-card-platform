package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.VirtualCardsCsvRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VcpCardProviderProcessor implements ItemProcessor<VirtualCardsCsvRow, CardProvider> {

    private final BatchCacheUtils batchCacheUtils;

    @Override
    public CardProvider process(VirtualCardsCsvRow csvRow) {

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
        return cardProvider;
    }
}