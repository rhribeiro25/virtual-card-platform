package br.com.rhribeiro25.virtual_card_platform.application.port.out;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.application.dto.CardResponse;

public class OutputCardMapper {


    public static CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getHolderName(),
                card.getStatus(),
                card.getBalance(),
                card.getCreatedAt()
        );
    }
}
