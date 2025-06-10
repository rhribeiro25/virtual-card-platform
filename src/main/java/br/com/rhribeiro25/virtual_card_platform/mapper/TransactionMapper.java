package br.com.rhribeiro25.virtual_card_platform.mapper;

import br.com.rhribeiro25.virtual_card_platform.dto.TransactionRequest;
import br.com.rhribeiro25.virtual_card_platform.dto.TransactionResponse;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.model.TransactionType;

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
