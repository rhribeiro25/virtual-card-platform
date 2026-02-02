//package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers;
//
//import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardProviderUsecase;
//import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
//import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
//import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
//import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
//import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Slf4j
//@Component(SpringBatchWriter.CARD_PROVIDER)
//@StepScope
//public class CardProviderWriter extends AbstractBatchWriter<CardProvider> {
//
//    private final CardProviderUsecase cardProviderUsecase;
//
//    public CardProviderWriter(CardProviderUsecase cardProviderUsecase,
//                              BatchAuditMongoTemplate batchAuditMongoTemplate) {
//        super(batchAuditMongoTemplate);
//        this.cardProviderUsecase = cardProviderUsecase;
//    }
//
//    @Override
//    protected String getWriterName() {
//        return SpringBatchWriter.CARD_PROVIDER;
//    }
//
//    @Override
//    protected String getField() {
//        return BatchAuditImport.Fields.persistedCardProviderId;
//    }
//
//    @Override
//    protected CardProvider extractEntity(BatchAuditImport item) {
//        return item.getCardProvider();
//    }
//
//    @Override
//    protected UUID saving(CardProvider entity, ActionType actionType) {
//        return cardProviderUsecase.upsert(entity, actionType);
//    }
//
//}
