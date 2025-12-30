package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import lombok.Builder;

@Builder
public record VcpModelsGroup (
        Card card,
        Transaction transaction,
        Provider provider,
        CardProvider cardProvider
){}