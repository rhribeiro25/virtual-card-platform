package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.*;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.CardProviderRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.ProviderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component(SpringBatchProcessor.CARD_PROVIDER)
@StepScope
@RequiredArgsConstructor
public class VcpCardProviderProcessor implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    private final CardRepository cardRepository;
    private final ProviderRepository providerRepository;
    private final CardProviderRepository cardProviderRepository;

    @Override
    public BatchAuditImport process(BatchAuditImport batchAuditImport) throws JsonProcessingException {

        CsvFileRow csvFileRow = batchAuditImport.getCsvFileRow();
        Optional<Card> card = getCardByExternalId(batchAuditImport);
        Optional<Provider> provider = getProviderByCode(batchAuditImport);

        if (card.isEmpty() || provider.isEmpty()) return null;
        if (cardProviderRepository.existsByCardAndProvider(card.get(), provider.get())) return batchAuditImport;

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

    private Optional<Card> getCardByExternalId(BatchAuditImport item) {
        if (item.getCardRef().isEmpty()) return Optional.empty();
        return cardRepository.findByExternalId(item.getCardRef());
    }

    private Optional<Provider> getProviderByCode(BatchAuditImport item) {
        if (item.getProviderCode().isEmpty()) return Optional.empty();
        return providerRepository.findByCode(item.getProviderCode());
    }
}
