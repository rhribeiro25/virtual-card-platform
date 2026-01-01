package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardBrand;
import br.com.rhribeiro25.virtual_card_platform.domain.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Card {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String externalId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Enumerated(EnumType.STRING)
    private CardBrand brand;

    @Column(nullable = false)
    private String holderName;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = true)
    private String pinCode; // optional PIN code

    @Column(nullable = true)
    private LocalDateTime expiryDate; // expiration date

    @Column(nullable = true)
    private Integer cvv; // security code

    @Column(nullable = true)
    private String country; // country of issuance

    @Column(nullable = true)
    private String currency; // primary currency

    @Column(nullable = false)
    private Boolean internationalAllowed; // allows international transactions

    @Column(nullable = true)
    private Integer maxDailyTransactions; // daily transaction limit

    @Column(nullable = true)
    private BigDecimal maxTransactionAmount; // maximum per transaction

    @Column(nullable = true)
    private String notes; // internal or administrative notes

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<CardProvider> cardProviders;

    @Version
    private Long version = 0L;

    @Transient
    private String auditId;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = CardStatus.ACTIVE;
        }
    }
}
