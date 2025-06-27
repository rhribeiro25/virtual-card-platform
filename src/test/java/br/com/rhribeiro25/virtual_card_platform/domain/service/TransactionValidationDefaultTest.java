package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionValidationDefaultTest {

    TransactionValidation validation = new TransactionValidation() {
        @Override
        public void validate(Transaction transaction) {}
    };

    @Test
    void testSupportsDefault() {
        for (TransactionType type : TransactionType.values()) {
            assertTrue(validation.supports(type));
        }
    }
}

