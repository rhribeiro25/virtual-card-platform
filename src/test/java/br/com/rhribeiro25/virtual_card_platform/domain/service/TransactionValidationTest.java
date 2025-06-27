package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class TransactionValidationTest {

    @InjectMocks
    private final TransactionStatusValidationImpl validation = new TransactionStatusValidationImpl();

    @Test
    @DisplayName("supports() returns true only for SPEND and TOPUP transaction type")
    void supportsShouldReturnTrueOnlyForSpend() {
        Assertions.assertEquals(true, validation.supports(TransactionType.SPEND));
        Assertions.assertEquals(true, validation.supports(TransactionType.TOPUP));
    }
}
