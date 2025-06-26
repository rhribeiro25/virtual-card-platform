package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @Column(nullable = false, unique = true)
    private UUID requestId;

    public Transaction() {
    }

    private Transaction(Transaction.Builder builder) {
        this.id = builder.id;
        this.card = builder.card;
        this.type = builder.type;
        this.amount = builder.amount;
        this.createdAt = builder.createdAt;
        this.requestId = builder.requestId;
    }

    public UUID getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public static class Builder {

        private UUID id;
        private Card card;
        private TransactionType type;
        private BigDecimal amount;
        private Timestamp createdAt;
        private UUID requestId;

        public Transaction.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Transaction.Builder card(Card card) {
            this.card = card;
            return this;
        }

        public Transaction.Builder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public Transaction.Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Transaction.Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Transaction.Builder requestId(UUID requestId) {
            this.requestId = requestId;
            return this;
        }


        public Transaction build() {
            return new Transaction(this);
        }
    }
}