package br.com.rhribeiro25.virtual_card_platform.application.template;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.service.TransactionValidation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SpendTransactionProcessor extends TransactionTemplate {

    private final List<TransactionValidation> validations;

    public SpendTransactionProcessor(List<TransactionValidation> validations) {
        this.validations = validations;
    }

    @Override
    protected void validate(Card card, BigDecimal amount) {
        validations.forEach(v -> v.validate(card, amount, TransactionType.SPEND));
    }

    @Override
    protected void updateBalance(Card card, BigDecimal amount) {
        card.setBalance(card.getBalance().subtract(amount));
    }

    @Override
    protected TransactionType getType() {
        return TransactionType.SPEND;
    }
}
