package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.processor;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto.ExternalCardCsvRow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class VcpItemProcessor implements ItemProcessor<ExternalCardCsvRow, Card> {

    @Override
    public Card process(ExternalCardCsvRow externalCard) {

//        if (!externalCard.state().equalsIgnoreCase("A")) {
//            throw new IllegalArgumentException("Deactivated Card!");
//        }

        // normalização simples
//        card.setFirstName(card.getFirstName().trim());
//        card.setLastName(card.getLastName().trim());

        return Card.builder().build();
    }
}
