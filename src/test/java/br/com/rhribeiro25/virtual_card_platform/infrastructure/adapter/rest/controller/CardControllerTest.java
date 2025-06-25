package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.controller;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.CardRequest;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.TransactionRequest;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.persistence.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CardControllerTest {

    private static final String BASE_PATH = "/cards";
    private static final BigDecimal VALID_AMOUNT = BigDecimal.valueOf(50);
    private static final BigDecimal INVALID_AMOUNT = BigDecimal.valueOf(-10);
    private static final String VALID_NAME = "William";
    private static final String INVALID_NAME = "";
    private static final String ENDPOINT_SPEND =  "spend";
    private static final String ENDPOINT_TOPUP =  "topup";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardController cardController;

    @Autowired
    private ObjectMapper objectMapper;

    private Card card;

    @Autowired
    private CardUsecase cardUsecase;

    @BeforeEach
    void setup() {
        card = new Card.Builder()
                .cardholderName("Renan")
                .balance(BigDecimal.valueOf(200))
                .build();
        card = cardUsecase.create(card);

    }

    @Test
    @DisplayName("Should create a card successfully")
    void createCardShouldSucceed() throws Exception {
        CardRequest request = new CardRequest(VALID_NAME, VALID_AMOUNT);
        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardholderName").value(VALID_NAME));
    }

    @Test
    @DisplayName("Should fail to create card without required attributes")
    void createCardShouldFailWithoutRequiredAttributes() throws Exception {
        CardRequest request = new CardRequest(INVALID_NAME, VALID_AMOUNT);
        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should complete a SPEND transaction successfully")
    void spendShouldSucceed() throws Exception {
        performTransactionPost(card.getId(),  ENDPOINT_SPEND, VALID_AMOUNT)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("150.0"))
                .andExpect(jsonPath("$.cardholderName").value("Renan"));
    }

    @Test
    @DisplayName("Should fail SPEND with insufficient balance")
    void spendShouldFailWithInsufficientBalance() throws Exception {
        performTransactionPost(card.getId(),  ENDPOINT_SPEND, BigDecimal.valueOf(250))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail SPEND with invalid amount")
    void spendShouldFailWithInvalidAmount() throws Exception {
        performTransactionPost(card.getId(),  ENDPOINT_SPEND, INVALID_AMOUNT)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail on duplicate SPEND transaction")
    void spendShouldFailWithDuplicate() throws Exception {
        performTransactionPost(card.getId(),  ENDPOINT_SPEND, VALID_AMOUNT)
                .andExpect(status().isOk());

        performTransactionPost(card.getId(),  ENDPOINT_SPEND, VALID_AMOUNT)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should enforce SPEND rate limit of 5 per minute")
    void spendShouldRespectRateLimit() throws Exception {
        for (int i = 1; i <= 5; i++) {
            performTransactionPost(card.getId(),  ENDPOINT_SPEND, BigDecimal.valueOf(i))
                    .andExpect(status().isOk());
        }

        performTransactionPost(card.getId(),  ENDPOINT_SPEND, BigDecimal.valueOf(6))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail SPEND when card is blocked")
    void spendShouldFailWhenCardIsBlocked() throws Exception {
        card.setStatus(CardStatus.BLOCKED);
        cardUsecase.create(card);

        performTransactionPost(card.getId(),  ENDPOINT_SPEND, VALID_AMOUNT)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should complete a TOPUP transaction successfully")
    void topupShouldSucceed() throws Exception {
        performTransactionPost(card.getId(),  ENDPOINT_TOPUP, BigDecimal.valueOf(300))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("500.0"))
                .andExpect(jsonPath("$.cardholderName").value("Renan"));
    }

    @Test
    @DisplayName("Should fail TOPUP with invalid amount")
    void topupShouldFailWithInvalidAmount() throws Exception {
        performTransactionPost(card.getId(),  ENDPOINT_TOPUP, INVALID_AMOUNT)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail on duplicate TOPUP transaction")
    void topupShouldFailWithDuplicate() throws Exception {
        performTransactionPost(card.getId(),  ENDPOINT_TOPUP, VALID_AMOUNT)
                .andExpect(status().isOk());

        performTransactionPost(card.getId(),  ENDPOINT_TOPUP, VALID_AMOUNT)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should enforce TOPUP rate limit of 5 per minute")
    void topupShouldRespectRateLimit() throws Exception {
        for (int i = 1; i <= 5; i++) {
            performTransactionPost(card.getId(),  ENDPOINT_TOPUP, BigDecimal.valueOf(i))
                    .andExpect(status().isOk());
        }

        performTransactionPost(card.getId(),  ENDPOINT_TOPUP, BigDecimal.valueOf(6))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail TOPUP when card is blocked")
    void topupShouldFailWhenCardIsBlocked() throws Exception {
        card.setStatus(CardStatus.BLOCKED);
        cardUsecase.create(card);

        performTransactionPost(card.getId(),  ENDPOINT_TOPUP, VALID_AMOUNT)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return card By ID Successfully")
    void shouldReturnCardByIdSuccessfully() throws Exception {

        UUID cardId = card.getId();
        String expectedName = card.getCardholderName();

        mvc.perform(get(BASE_PATH + "/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardholderName").value(expectedName));
    }

    @Test
    @DisplayName("Should return card By ID Not Found")
    void shouldReturnNotFoundWhenCardDoesNotExist() throws Exception {

        UUID nonexistentId = UUID.randomUUID();

        mvc.perform(get(BASE_PATH + "/" + nonexistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return paginated transactions for valid card")
    void shouldReturnTransactionsSuccessfully() throws Exception {

        Transaction transaction = new Transaction.Builder()
                .card(card)
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.SPEND)
                .build();

        transactionRepository.save(transaction);

        mvc.perform(get(BASE_PATH + "/" + card.getId() + "/transactions")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].amount").value("50.0"))
                .andExpect(jsonPath("$.content[0].type").value("SPEND"));
    }

    @Test
    @DisplayName("Should return empty transaction page when none exist")
    void shouldReturnEmptyTransactionList() throws Exception {
        mvc.perform(get(BASE_PATH + "/" + card.getId() + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    private ResultActions performTransactionPost(UUID id, String endpoint, BigDecimal amount) throws Exception {
        TransactionRequest request = new TransactionRequest(amount);
        return mvc.perform(post( BASE_PATH + "/" + id + "/" + endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)));
    }

    private String asJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
