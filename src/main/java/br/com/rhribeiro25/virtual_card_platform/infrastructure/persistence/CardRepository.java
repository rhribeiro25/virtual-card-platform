package br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {}
