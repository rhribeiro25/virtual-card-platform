package br.com.rhribeiro25.virtual_card_platform.application.template;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.domain.service.TransactionValidation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpendTransactionProcessor extends TransactionTemplate {

    private final List<TransactionValidation> validations;

    public SpendTransactionProcessor(List<TransactionValidation> validations) {
        this.validations = validations.stream()
                .filter(v -> v.supports(TransactionType.SPEND))
                .collect(Collectors.toList());
    }

    @Override
    protected void validate(Transaction transaction) {
        validations.forEach(v -> v.validate(transaction));
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
