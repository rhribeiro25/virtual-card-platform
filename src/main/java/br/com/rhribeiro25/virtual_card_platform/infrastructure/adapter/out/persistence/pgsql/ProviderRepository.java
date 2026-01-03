package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    boolean existsByCode(String code);

    Optional<Provider> findByCode(String code);
}
