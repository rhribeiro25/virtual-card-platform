package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.persistence;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    Optional<Provider> findByCode(String code);

    boolean existsByCode(String code);
}
