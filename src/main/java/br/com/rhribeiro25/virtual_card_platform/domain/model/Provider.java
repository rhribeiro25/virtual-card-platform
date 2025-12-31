package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.enums.ProviderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Provider {

    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private String country;

    @Enumerated(EnumType.STRING)
    private ProviderStatus status;

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<CardProvider> cardProviders;
}

