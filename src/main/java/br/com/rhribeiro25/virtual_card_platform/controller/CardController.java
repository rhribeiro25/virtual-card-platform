package br.com.rhribeiro25.virtual_card_platform.controller;

import br.com.rhribeiro25.virtual_card_platform.dto.CardRequest;
import br.com.rhribeiro25.virtual_card_platform.dto.CardResponse;
import br.com.rhribeiro25.virtual_card_platform.dto.TransactionRequest;
import br.com.rhribeiro25.virtual_card_platform.dto.TransactionResponse;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.service.CardService;
import br.com.rhribeiro25.virtual_card_platform.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final TransactionService transactionService;

    @Autowired
    public CardController(CardService cardService, TransactionService transactionService) {
        this.cardService = cardService;
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@RequestBody CardRequest request) {
        Card card = cardService.create(request.cardholderName(), request.initialBalance());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CardResponse(card.getId(), card.getCardholderName(), card.getBalance(), card.getCreatedAt()));
    }

    @PostMapping("/{id}/spend")
    public ResponseEntity<?> spend(@PathVariable UUID id, @RequestBody TransactionRequest request) {
        try {
            Card card = cardService.spend(id, request.amount());
            return ResponseEntity.ok(new CardResponse(card.getId(), card.getCardholderName(), card.getBalance(), card.getCreatedAt()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<CardResponse> topUp(@PathVariable UUID id, @RequestBody TransactionRequest request) {
        Card card = cardService.topUp(id, request.amount());
        return ResponseEntity.ok(new CardResponse(card.getId(), card.getCardholderName(), card.getBalance(), card.getCreatedAt()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id) {
        Card card = cardService.getCardById(id);
        return ResponseEntity.ok(new CardResponse(card.getId(), card.getCardholderName(), card.getBalance(), card.getCreatedAt()));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionsPage = transactionService.getTransactionsByCardId(id, pageable);

        Page<TransactionResponse> responsePage = transactionsPage.map(tx ->
                new TransactionResponse(
                        tx.getId(),
                        tx.getCard().getId(),
                        tx.getType().name(),
                        tx.getAmount(),
                        tx.getCreatedAt()
                )
        );

        return ResponseEntity.ok(responsePage);
    }
}