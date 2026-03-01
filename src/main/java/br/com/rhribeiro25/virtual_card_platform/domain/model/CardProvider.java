package br.com.rhribeiro25.virtual_card_platform.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "card_provider", uniqueConstraints = {@UniqueConstraint(columnNames = {"card_id", "provider_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CardProvider extends AbstractModel {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal feePercentage;

    @Column(nullable = false)
    private BigDecimal dailyLimit;

    @Column(nullable = false)
    private Integer priority;

    @Transient
    private String key;

    public String getKey() {
        return card.getExternalId() + "_" + provider.getCode();
    }
}
