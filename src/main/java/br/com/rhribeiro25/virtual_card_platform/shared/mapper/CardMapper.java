package br.com.rhribeiro25.virtual_card_platform.shared.mapper;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.CardRequest;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.CardResponse;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;

public class CardMapper {

    public static Card toEntity(CardRequest request) {
        return new Card.Builder()
                .cardholderName(request.cardholderName())
                .balance(request.initialBalance())
                .build();
    }

    public static CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getCardholderName(),
                card.getStatus(),
                card.getBalance(),
                card.getCreatedAt()
        );
    }
}
