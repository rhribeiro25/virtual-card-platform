package br.com.rhribeiro25.virtual_card_platform.application.usecase;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.pgsql.CardProviderRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.InternalServerErrorException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CardProviderUsecase {

    private final CardProviderRepository cardProviderRepository;

    /*******************************************************************************************************************
     SPRING BATCH METHODS
     ********************************************************************************************************************/
    public CardProvider saveByBatch(CardProvider cardProvider) {
        try {
            return cardProviderRepository.save(cardProvider);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new ConflictException(MessageUtils.getMessage("card.provider.conflict"));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    public boolean existsByCardAndProvider(Card card, Provider provider) {
        return cardProviderRepository.existsByCardAndProvider(card, provider);
    }
}
