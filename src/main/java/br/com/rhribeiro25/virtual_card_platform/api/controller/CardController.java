package br.com.rhribeiro25.virtual_card_platform.api.controller;

import br.com.rhribeiro25.virtual_card_platform.api.dto.CardRequest;
import br.com.rhribeiro25.virtual_card_platform.api.dto.CardResponse;
import br.com.rhribeiro25.virtual_card_platform.api.dto.TransactionRequest;
import br.com.rhribeiro25.virtual_card_platform.api.dto.TransactionResponse;
import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.shared.mapper.CardMapper;
import br.com.rhribeiro25.virtual_card_platform.shared.mapper.TransactionMapper;
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

    private final CardUsecase cardUsecase;

    @Autowired
    public CardController(CardUsecase cardUsecase) {
        this.cardUsecase = cardUsecase;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request) {
        Card card = cardUsecase.create(CardMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CardMapper.toResponse(card));
    }

    @PostMapping("/{id}/spend")
    public ResponseEntity<?> spend(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        Card card = cardUsecase.spend(id, request.amount());
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<CardResponse> topUp(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        Card card = cardUsecase.topUp(id, request.amount());
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id) {
        Card card = cardUsecase.getCardById(id);
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionsPage = cardUsecase.getTransactionsByValidCardId(id, pageable);

        Page<TransactionResponse> responsePage = transactionsPage.map(TransactionMapper::toResponse);

        return ResponseEntity.ok(responsePage);
    }
}