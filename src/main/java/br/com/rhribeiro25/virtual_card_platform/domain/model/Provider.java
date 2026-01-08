package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ProviderStatus;
import br.com.rhribeiro25.virtual_card_platform.shared.utils.StringUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Provider extends AbstractModel {

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return Objects.equals(code, provider.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

}

