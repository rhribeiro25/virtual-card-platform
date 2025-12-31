package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processors;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.VirtualCardsCsvRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.utils.BatchCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VcpTransactionProcessor implements ItemProcessor<VirtualCardsCsvRow, Transaction> {

    private final BatchCacheUtils batchCacheUtils;

    @Override
    public Transaction process(VirtualCardsCsvRow csvRow) {

        Transaction transaction = batchCacheUtils.transactionCache().get(csvRow.getTxRequestRef());
        if (transaction == null) {
            transaction = Transaction.builder()
                    .type(mapTransactionType(csvRow.getTxKind()))
                    .createdAt(LocalDateTime.now())
                    .amount(new BigDecimal(csvRow.getTxAmountTxt().replace(",", ".")))
                    .requestId(UUID.fromString(csvRow.getTxRequestRef()))
                    .cardExternalId(csvRow.getCardRef())
                    .build();
        }
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