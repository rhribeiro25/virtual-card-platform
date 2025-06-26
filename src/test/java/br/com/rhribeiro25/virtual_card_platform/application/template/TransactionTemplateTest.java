
package br.com.rhribeiro25.virtual_card_platform.application.template;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.TransactionUsecase;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.CardRepository;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TransactionTemplateTest {

    @Test
    @DisplayName("Should throw ConflictException when card save fails")
    void shouldThrowOptimisticLockExceptionOnSaveFailure() {
        TransactionTemplate template = new TransactionTemplate() {
            @Override
            protected void validate(Transaction transaction) {}

            @Override
            protected void updateBalance(Card card, BigDecimal amount) {}

            @Override
            protected TransactionType getType() {
                return TransactionType.SPEND;
            }
        };

        CardRepository cardRepository = mock(CardRepository.class);
        TransactionUsecase transactionUsecase = mock(TransactionUsecase.class);

        ReflectionTestUtils.setField(template, "cardRepository", cardRepository);
        ReflectionTestUtils.setField(template, "transactionUsecase", transactionUsecase);

        Card card = new Card.Builder()
                .cardholderName("Test User")
                .balance(BigDecimal.valueOf(100))
                .build();

        doThrow(new ConflictException("DB Error")).when(cardRepository).save(any());

        try (MockedStatic<MessageUtils> mocked = mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.getMessage("card.conflict"))
                  .thenReturn("Card already updated by another transaction");
            Transaction transaction = new Transaction.Builder().card(card).amount(BigDecimal.TEN).build();
            assertThrows(ConflictException.class, () -> template.process(transaction));
        }
    }
}
