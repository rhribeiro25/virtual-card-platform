package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardProviderRepository extends JpaRepository<CardProvider, UUID> {
    boolean existsByCardAndProvider(Card card, Provider provider);

}
