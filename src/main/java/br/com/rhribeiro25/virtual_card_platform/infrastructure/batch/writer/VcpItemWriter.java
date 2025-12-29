package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writer;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VcpItemWriter {

    private final CardRepository cardRepository;

    @Bean
    public ItemWriter<Card> write() {
        return cardRepository::saveAll;
    }
}
