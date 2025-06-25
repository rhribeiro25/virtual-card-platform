package br.com.rhribeiro25.virtual_card_platform.application.template;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.service.TransactionValidation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TopUpTransactionProcessor extends TransactionTemplate {

    private final List<TransactionValidation> validations;

    public TopUpTransactionProcessor(List<TransactionValidation> validations) {
        this.validations = validations.stream()
                .filter(v -> v.supports(TransactionType.TOPUP.name()))
                .collect(Collectors.toList());
    }

    @Override
    protected void validate(Card card, BigDecimal amount) {
        validations.forEach(v -> v.validate(card, amount, TransactionType.TOPUP));
    }

    @Override
    protected void updateBalance(Card card, BigDecimal amount) {
        card.setBalance(card.getBalance().add(amount));
    }

    @Override
    protected TransactionType getType() {
        return TransactionType.TOPUP;
    }
}
