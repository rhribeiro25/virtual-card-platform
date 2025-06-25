package br.com.rhribeiro25.virtual_card_platform.shared.mapper;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.TransactionResponse;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;

import java.math.BigDecimal;

public class TransactionMapper {

    public static Transaction toEntity(BigDecimal amount, Card card, TransactionType type) {
        return new Transaction.Builder()
                .card(card)
                .amount(amount)
                .type(type)
                .build();
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCard().getId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getCreatedAt()
        );
    }

}
