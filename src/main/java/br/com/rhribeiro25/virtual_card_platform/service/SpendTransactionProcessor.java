package br.com.rhribeiro25.virtual_card_platform.service;

import br.com.rhribeiro25.virtual_card_platform.model.*;
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
