package br.com.rhribeiro25.virtual_card_platform.application.port.in;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.application.dto.CardRequest;

import java.util.UUID;

public class InputCardMapper {

    public static Card toEntity(CardRequest request) {
        return Card.builder()
                .holderName(request.cardholderName())
                .externalId(UUID.randomUUID().toString())
                .status(request.isActive() ? CardStatus.ACTIVE : CardStatus.BLOCKED)
                .internationalAllowed(request.isInternationalAllowed())
                .createdAt(request.createdAt())
                .balance(request.initialBalance())
                .build();
    }

}
