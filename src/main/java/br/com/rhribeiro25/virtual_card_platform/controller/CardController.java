package br.com.rhribeiro25.virtual_card_platform.controller;

import br.com.rhribeiro25.virtual_card_platform.dto.CardRequest;
import br.com.rhribeiro25.virtual_card_platform.dto.CardResponse;
import br.com.rhribeiro25.virtual_card_platform.dto.TransactionRequest;
import br.com.rhribeiro25.virtual_card_platform.dto.TransactionResponse;
import br.com.rhribeiro25.virtual_card_platform.mapper.CardMapper;
import br.com.rhribeiro25.virtual_card_platform.mapper.TransactionMapper;
import br.com.rhribeiro25.virtual_card_platform.model.Card;
import br.com.rhribeiro25.virtual_card_platform.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.service.CardService;
import jakarta.validation.Valid;
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

    @Autowired
    public CardController(CardService cardService ) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request) {
        Card card = cardService.create(CardMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CardMapper.toResponse(card));
    }

    @PostMapping("/{id}/spend")
    public ResponseEntity<?> spend(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        try {
            Card card = cardService.spend(id, request.amount());
            return ResponseEntity.ok(CardMapper.toResponse(card));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<CardResponse> topUp(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        Card card = cardService.topUp(id, request.amount());
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id) {
        Card card = cardService.getCardById(id);
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionsPage = cardService.getTransactionsByValidCardId(id, pageable);

        Page<TransactionResponse> responsePage = transactionsPage.map(TransactionMapper::toResponse);

        return ResponseEntity.ok(responsePage);
    }
}