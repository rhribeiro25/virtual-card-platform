package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardBrand;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.StringUtils.*;

@Entity
@Table(name = "cards")
@Getter
@Setter()
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Card extends AbstractModel {

    @Column(unique = true)
    private String externalId;

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

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = CardStatus.ACTIVE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(externalId, card.externalId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(externalId);
    }
}
