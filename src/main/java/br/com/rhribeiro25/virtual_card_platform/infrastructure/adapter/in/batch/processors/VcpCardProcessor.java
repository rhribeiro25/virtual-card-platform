package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.domain.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy.ActionTypeStrategyFactory;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.BigDecimalUtils;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpringBatchProcessor.CARD)
@StepScope
@RequiredArgsConstructor
public class VcpCardProcessor extends VcpAbstractBatchProcessor<Card> {

    private final CardService cardService;
    private final BigDecimalUtils bigDecimalUtils;
    private final DateUtils dateUtils;
    private final ActionTypeStrategyFactory<Card, BatchAuditImport> strategyFactory;

    @Override
    protected boolean dependenciesResolved(BatchAuditImport item) {
        return true;
    }

    @Override
    protected Card buildEntity(ActionType actionType, BatchAuditImport item) {
        if (!actionType.equals(ActionType.NEW)) return null;
        return strategyFactory
                .getStrategy(actionType, Card.class)
                .execute(item);
    }

    @Override
    protected void attachEntity(BatchAuditImport item, Card card) {
        item.setCard(card);
    }

}