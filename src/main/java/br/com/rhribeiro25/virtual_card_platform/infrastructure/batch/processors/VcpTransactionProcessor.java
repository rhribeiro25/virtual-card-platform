package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.CsvRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpTransactionProcessor implements ItemProcessor<AuditImport, Transaction> {

    private final BatchCacheUtils batchCacheUtils;
    private final ObjectMapper objectMapper;

    @Override
    public Transaction process(AuditImport auditImport) throws JsonProcessingException {

        CsvRow csvRow = objectMapper.readValue(auditImport.getRawPayload(), CsvRow.class);

        Transaction transaction = batchCacheUtils.transactionCache().get(csvRow.getTxRequestRef());
        if (transaction == null) {
            Card card = batchCacheUtils.cardCache().get(csvRow.getCardRef());
            transaction = Transaction.builder()
                    .card(card)
                    .type(mapTransactionType(csvRow.getTxKind()))
                    .createdAt(LocalDateTime.now())
                    .amount(new BigDecimal(csvRow.getTxAmountTxt().replace(",", ".")))
                    .requestId(UUID.fromString(csvRow.getTxRequestRef()))
                    .cardExternalId(csvRow.getCardRef())
                    .build();
        }
        batchCacheUtils.auditCache().put(auditImport.getId().toString(), auditImport);
        return transaction;
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