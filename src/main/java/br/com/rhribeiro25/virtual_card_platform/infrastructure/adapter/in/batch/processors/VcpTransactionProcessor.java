package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.domain.service.TransactionService;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component(SpringBatchProcessor.TRANSACTION)
@StepScope
@RequiredArgsConstructor
public class VcpTransactionProcessor extends VcpAbstractBatchProcessor<Transaction> {

    private final CardUsecase cardUsecase;
    private final TransactionUsecase transactionUsecase;
    private final TransactionService transactionService;

    @Override
    protected boolean dependenciesResolved(BatchAuditImport item) {
        return cardUsecase.getCardByExternalId(item.getCardRef()).isPresent();
    }

    @Override
    protected Transaction buildEntity(CsvFileRow row, BatchAuditImport item) {
        Card card = cardUsecase.getCardByExternalId(item.getCardRef()).orElseThrow();

        return Transaction.builder()
                .type(transactionService.mapType(row.getTxKind()))
                .createdAt(LocalDateTime.now())
                .amount(new BigDecimal(row.getTxAmountTxt().replace(",", ".")))
                .requestId(row.getTxRequestRef())
                .card(card)
                .build();
    }

    @Override
    protected void attachEntity(BatchAuditImport item, Transaction transaction) {
        item.setTransaction(transaction);
    }


}