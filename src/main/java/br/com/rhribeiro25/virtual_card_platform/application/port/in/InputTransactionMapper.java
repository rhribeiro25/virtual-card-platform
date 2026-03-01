package br.com.rhribeiro25.virtual_card_platform.application.port.in;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.application.dto.TransactionRequest;

public class InputTransactionMapper {

    public static Transaction toEntity(TransactionRequest transaction, Card card, TransactionType type) {
        return Transaction.builder()
                .card(card)
                .amount(transaction.amount())
                .requestId(transaction.requestId())
                .type(type)
                .createdAt(transaction.createdAt())
                .build();
    }

}
