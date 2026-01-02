package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardProviderRepository extends JpaRepository<CardProvider, UUID> {
}
