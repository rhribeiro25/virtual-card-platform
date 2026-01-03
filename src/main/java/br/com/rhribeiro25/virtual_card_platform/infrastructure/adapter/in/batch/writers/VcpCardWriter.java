package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchWriter;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component(SpringBatchWriter.CARD)
@StepScope
@RequiredArgsConstructor
public class VcpCardWriter implements ItemWriter<BatchAuditImport> {

    private final CardRepository cardRepository;
    private final BatchAuditMongoTemplate batchAuditMongoTemplate;

    @Override
    public void write(Chunk<? extends BatchAuditImport> chunk) {
        List<String> chunkCheck = new ArrayList<>();
        for (BatchAuditImport item : chunk.getItems()) {
            Card card = item.getCard();
            item.setAuxFlag(true);
            if (card != null && !chunkCheck.contains(card.getExternalId())) {
                try {
                    chunkCheck.add(card.getExternalId());
                    cardRepository.save(card);
                } catch (DataIntegrityViolationException e) {
                    item.setAuxFlag(false);
                    log.warn("Card already exists: {}", item.getId());
                }
            }
            batchAuditMongoTemplate.updateProcessedFlag(item, "isProcessedCard");
        }
    }
}
