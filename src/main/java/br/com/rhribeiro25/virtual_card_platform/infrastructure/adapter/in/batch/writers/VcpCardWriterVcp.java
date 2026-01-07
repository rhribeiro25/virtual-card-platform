package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpringBatchWriter.CARD)
@StepScope
public class VcpCardWriterVcp extends VcpAbstractBatchWriter<Card, String> {

    private final CardUsecase cardUsecase;

    public VcpCardWriterVcp(CardUsecase cardUsecase,
                            BatchAuditMongoTemplate batchAuditMongoTemplate) {
        super(batchAuditMongoTemplate);
        this.cardUsecase = cardUsecase;
    }

    @Override
    protected String writerName() {
        return SpringBatchWriter.CARD;
    }

    @Override
    protected Card extractEntity(BatchAuditImport item) {
        return item.getCard();
    }

    @Override
    protected String extractKey(Card card) {
        return card.getExternalId();
    }

    @Override
    protected void save(Card card) {
        cardUsecase.saveByBatch(card);
    }

    @Override
    protected String processedFlag() {
        return BatchAuditImport.Fields.isProcessedCard;
    }
}
