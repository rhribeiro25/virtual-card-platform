package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.ProviderStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.VirtualCardsCsvRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VcpProviderProcessor implements ItemProcessor<VirtualCardsCsvRow, Provider> {

    private final BatchCacheUtils batchCacheUtils;

    @Override
    public Provider process(VirtualCardsCsvRow csvRow) {

       Provider provider = batchCacheUtils.providerCache().get(csvRow.getProviderCode());
        if (provider == null) {
            provider = Provider.builder()
                    .code(csvRow.getProviderCode())
                    .createdAt(LocalDateTime.now())
                    .status(mapProviderStatus(csvRow.getProviderState()))
                    .country(csvRow.getProviderCountry())
                    .build();
        }
        return provider;
    }

    private ProviderStatus mapProviderStatus(String state) {

        return switch (state) {
            case "A" -> ProviderStatus.ACTIVE;
            case "B" -> ProviderStatus.BLOCKED;
            default -> throw new IllegalArgumentException(
                    "Invalid Provider: " + state
            );
        };
    }

}