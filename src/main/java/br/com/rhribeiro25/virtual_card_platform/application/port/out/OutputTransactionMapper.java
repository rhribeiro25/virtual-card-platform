package br.com.rhribeiro25.virtual_card_platform.application.port.out;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.application.dto.TransactionResponse;

public class OutputTransactionMapper {

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
