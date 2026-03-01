package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.ProviderRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.InternalServerErrorException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.RedisConstants.PROVIDER_CACHE;

@Service
@AllArgsConstructor
public class ProviderUsecase {

    private final ProviderRepository providerRepository;

    /*******************************************************************************************************************
     SPRING BATCH METHODS
     ********************************************************************************************************************/
    @CachePut(
            value = PROVIDER_CACHE,
            key = "#provider.code"
    )
    public Provider saveByBatch(Provider provider) {
        try {
            return providerRepository.save(provider);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new ConflictException(MessageUtils.getMessage("provider.conflict"));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 30, multiplier = 2)
    )
    @Cacheable(
            value = PROVIDER_CACHE,
            key = "#code",
            unless = "#result == null"
    )
    public Provider getProviderByCode(String code) {
        return providerRepository.findByCode(code).orElse(null);
    }

    public boolean existsByCode(String code) {
        return providerRepository.existsByCode(code);
    }

    public Optional<UUID> findIdByCode(String code) {
        return providerRepository.findIdByCode(code);
    }

}
