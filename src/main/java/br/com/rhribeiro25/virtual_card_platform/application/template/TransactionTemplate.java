package br.com.rhribeiro25.virtual_card_platform.application.template;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;

public abstract class TransactionTemplate {

    @Autowired
    protected CardRepository cardRepository;

    @Autowired
    protected TransactionUsecase transactionUsecase;

    public final Card process(Transaction transaction) {
        validate(transaction);
        updateBalance(transaction.getCard(), transaction.getAmount());
        saveCard(transaction.getCard());
        createTransaction(transaction);
        return transaction.getCard();
    }

    protected abstract void validate(Transaction transaction);

    protected abstract void updateBalance(Card card, BigDecimal amount);

    protected abstract TransactionType getType();

    private void saveCard(Card card) {
        try {
            cardRepository.save(card);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new ConflictException(MessageUtils.getMessage("card.conflict"));
        }
    }

    private void createTransaction(Transaction transaction) {
        transactionUsecase.create(transaction);
    }
}
