package br.com.rhribeiro25.virtual_card_platform.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card_provider", uniqueConstraints = {@UniqueConstraint(columnNames = {"card_id", "provider_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CardProvider extends AbstractModel {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updatedAt;

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

    public String getKey(){
        return card.getExternalId() + "_" + provider.getCode();
    }
}
