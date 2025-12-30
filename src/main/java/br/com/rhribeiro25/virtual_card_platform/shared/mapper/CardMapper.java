package br.com.rhribeiro25.virtual_card_platform.shared.mapper;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.CardRequest;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.CardResponse;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;

import java.time.LocalDateTime;

public class CardMapper {

    public static Card toEntity(CardRequest request) {
        return Card.builder()
                .holderName(request.cardholderName())
                .status(request.isActive() ? CardStatus.ACTIVE : CardStatus.BLOCKED)
                .internationalAllowed(request.isInternationalAllowed())
                .createdAt(request.createdAt())
                .balance(request.initialBalance())
                .build();
    }

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
