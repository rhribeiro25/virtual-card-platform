package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardProviderRepository extends JpaRepository<CardProvider, UUID> {

    @Query("""
                select cp.id
                from CardProvider cp
                where cp.card.externalId = :enternalId
                  and cp.provider.code = :code
            """)
    Optional<UUID> findIdByCardAndProvider(
            @Param("enternalId") String externalId,
            @Param("code") String code
    );

    boolean existsByCardAndProvider(Card card, Provider provider);
}
