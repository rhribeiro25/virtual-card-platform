package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardBrand;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter()
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Card extends AbstractModel implements Mergeable<Card> {

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

    @Override
    public void mergeFrom(Card source) {

        this.setExternalId(source.getExternalId());

        if (source.getStatus() != null)
            this.setStatus(source.getStatus());

        if (source.getBalance() != null)
            this.setBalance(source.getBalance());

        if (source.getCvv() != null)
            this.setCvv(source.getCvv());

        if (StringUtils.hasText(source.getCountry()))
            this.setCountry(source.getCountry());

        if (source.getInternationalAllowed() != null)
            this.setInternationalAllowed(source.getInternationalAllowed());

        if (source.getMaxDailyTransactions() != null)
            this.setMaxDailyTransactions(source.getMaxDailyTransactions());

        if (source.getMaxTransactionAmount() != null)
            this.setMaxTransactionAmount(source.getMaxTransactionAmount());

        if (StringUtils.hasText(source.getNotes()))
            this.setNotes(source.getNotes());

    }

}
