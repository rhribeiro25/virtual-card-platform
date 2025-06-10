package br.com.rhribeiro25.virtual_card_platform.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String cardholderName;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.status = CardStatus.ACTIVE;
    }

    public Card() {}

    private Card(Builder builder) {
        this.id = builder.id;
        this.cardholderName = builder.cardholderName;
        this.balance = builder.balance;
        this.createdAt = builder.createdAt;
        this.transactions = builder.transactions;
        this.status = builder.status;
    }

    public UUID getId() {
        return id;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public static class Builder {

        private UUID id;
        private String cardholderName;
        private BigDecimal balance;
        private CardStatus status;
        private Timestamp createdAt;
        private List<Transaction> transactions;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder cardholderName(String cardholderName) {
            this.cardholderName = cardholderName;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder status(CardStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder transactions(List<Transaction> transactions) {
            this.transactions = transactions;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }

}