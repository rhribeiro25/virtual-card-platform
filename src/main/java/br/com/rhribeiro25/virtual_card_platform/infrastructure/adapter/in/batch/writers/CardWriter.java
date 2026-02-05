package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditImportMongoTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Slf4j
@Component
@StepScope
public class CardWriter extends AbstractBatchWriter<Card, String> {

    private final CardUsecase cardUsecase;

    public CardWriter(CardUsecase cardUsecase,
                      BatchAuditImportMongoTemplate batchAuditImportMongoTemplate) {
        super(batchAuditImportMongoTemplate);
        this.cardUsecase = cardUsecase;
    }

    @Override
    protected String getWriterName() {
        return getClassName(this.getClass());
    }

    @Override
    protected String getField() {
        return BatchAuditImport.Fields.cardId;
    }

    @Override
    protected Optional<Card> findExistingEntityByKey(String key) {
        return cardUsecase.findByExternalId(key);
    }

    @Override
    protected void mergeEntities(Card existing, Card incoming) {
        cardUsecase.merge(existing, incoming);
    }

    @Override
    protected Card extractEntity(BatchAuditImport item) {
        return item.getCard();
    }

    @Override
    protected String extractKey(Card entity) {
        return entity.getExternalId();
    }

    @Override
    protected void save(Card entity) {
        cardUsecase.saveByBatch(entity);
    }

}
