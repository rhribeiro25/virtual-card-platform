package br.com.rhribeiro25.virtual_card_platform.domain.model;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ProviderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Provider extends AbstractModel implements Serializable {

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

