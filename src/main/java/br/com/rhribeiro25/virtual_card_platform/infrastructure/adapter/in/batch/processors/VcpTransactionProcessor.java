package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.TransactionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component(SpringBatchProcessor.TRANSACTION)
@StepScope
@RequiredArgsConstructor
public class VcpTransactionProcessor implements ItemProcessor<BatchAuditImport, BatchAuditImport> {

    private final CardUsecase cardUsecase;
    private final TransactionUsecase transactionUsecase;

    @Override
    public BatchAuditImport process(BatchAuditImport batchAuditImport) throws JsonProcessingException {

        CsvFileRow csvFileRow = batchAuditImport.getCsvFileRow();
        Optional<Card> card = cardUsecase.getCardByExternalId(batchAuditImport.getCardRef());

        if (card.isEmpty()) return null;
        if (transactionUsecase.existsByRequestId(UUID.fromString(csvFileRow.getTxRequestRef())))
            return batchAuditImport;

        batchAuditImport.setTransaction(Transaction.builder()
                .type(mapTransactionType(csvFileRow.getTxKind()))
                .createdAt(LocalDateTime.now())
                .amount(new BigDecimal(csvFileRow.getTxAmountTxt().replace(",", ".")))
                .requestId(UUID.fromString(csvFileRow.getTxRequestRef()))
                .card(card.get())
                .build());

        return batchAuditImport;
    }

    private TransactionType mapTransactionType(String txKind) {

        return switch (txKind) {
            case "P" -> TransactionType.SPEND;
            case "C" -> TransactionType.TOPUP;
            case "T" -> TransactionType.TRANSFER;
            default -> throw new IllegalArgumentException(
                    "Invalid transaction type: " + txKind
            );
        };
    }


}