package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.ProviderRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.InternalServerErrorException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProviderUsecase {

    private final ProviderRepository providerRepository;

    @Cacheable(
            value = "provider-cache",
            key = "#code",
            unless = "#result == null"
    )
    public Optional<Provider> getProviderByCode(String code) {
        return providerRepository.findByCode(code);
    }

    @CachePut(
            value = "provider-cache",
            key = "#provider.code"
    )
    public Provider saveByBatch(Provider provider) {
        try {
            return providerRepository.save(provider);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new ConflictException(MessageUtils.getMessage("card.conflict"));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }
}
