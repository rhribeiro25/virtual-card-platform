package br.com.rhribeiro25.virtual_card_platform.mapper;

import br.com.rhribeiro25.virtual_card_platform.dto.CardRequest;
import br.com.rhribeiro25.virtual_card_platform.dto.CardResponse;
import br.com.rhribeiro25.virtual_card_platform.model.Card;

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
                card.getBalance(),
                card.getCreatedAt()
        );
    }
}
