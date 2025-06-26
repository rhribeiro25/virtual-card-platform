package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.controller;

import br.com.rhribeiro25.virtual_card_platform.application.usecase.CardUsecase;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.rest.dto.*;
import br.com.rhribeiro25.virtual_card_platform.shared.mapper.CardMapper;
import br.com.rhribeiro25.virtual_card_platform.shared.mapper.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create a new card")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card created successfully",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Initial balance must be zero or positive",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Selected card not found! Please verify your card information and try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 409,
                  "error": "Conflict",
                  "message": "Conflict detected: The card was modified by another transaction. Please try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "Unexpected error occurred. Please contact support.",
                  "path": "/cards"
                }
                """)))
    })
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request) {
        Card card = cardUsecase.create(CardMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CardMapper.toResponse(card));
    }

    @PostMapping("/{id}/spend")
    @Operation(summary = "Spend from a card")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spend successfully",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Initial balance must be zero or positive",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Selected card not found! Please verify your card information and try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 409,
                  "error": "Conflict",
                  "message": "Conflict detected: The card was modified by another transaction. Please try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "Unexpected error occurred. Please contact support.",
                  "path": "/cards"
                }
                """)))
    })
    public ResponseEntity<?> spend(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        Card card = cardUsecase.spend(id, request);
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @PostMapping("/{id}/topup")
    @Operation(summary = "Top-up a card")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topup successfully",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Initial balance must be zero or positive",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Selected card not found! Please verify your card information and try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 409,
                  "error": "Conflict",
                  "message": "Conflict detected: The card was modified by another transaction. Please try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "Unexpected error occurred. Please contact support.",
                  "path": "/cards"
                }
                """)))
    })
    public ResponseEntity<CardResponse> topUp(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        Card card = cardUsecase.topUp(id, request);
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Find by ID successfully",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Card ID is required to perform this operation.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Selected card not found! Please verify your card information and try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 409,
                  "error": "Conflict",
                  "message": "Conflict detected!",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "Unexpected error occurred. Please contact support.",
                  "path": "/cards"
                }
                """)))
    })
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id) {
        Card card = cardUsecase.getCardById(id);
        return ResponseEntity.ok(CardMapper.toResponse(card));
    }

    @GetMapping("/{id}/transactions")
    @Operation(summary = "Get transactions by card ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Find all transactions successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Card ID is required to perform this operation.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Transactions not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Transactions not found! Please verify your card information and try again.",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 409,
                  "error": "Conflict",
                  "message": "Conflict detected!",
                  "path": "/cards"
                }
                """))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-06-12T00:00:00Z",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "Unexpected error occurred. Please contact support.",
                  "path": "/cards"
                }
                """)))
    })
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
