package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardBrand;
import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.CardStatus;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.StringUtils.normalize;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    /*******************************************************************************************************************
    SPRING BATCH METHODS
    ********************************************************************************************************************/

    public CardBrand mapBrand(String brandCode) {

        return switch (normalize(brandCode)) {
            case "01" -> CardBrand.VISA;
            case "02" -> CardBrand.MASTERCARD;
            case "03" -> CardBrand.AMEX;
            case "04" -> CardBrand.ELO;
            case "05" -> CardBrand.HIPERCARD;
            default -> throw new IllegalArgumentException(
                    "Unknown brand code: " + brandCode
            );
        };
    }

    public CardStatus mapStatus(String state) {

        return switch (normalize(state)) {
            case "A" -> CardStatus.ACTIVE;
            case "B" -> CardStatus.BLOCKED;
            default -> throw new IllegalArgumentException(
                    "Invalid Card: " + state
            );
        };
    }

    public boolean mapBooleanAttribute(String bool) {

        return switch (normalize(bool)) {
            case "Y" -> true;
            case "N" -> false;
            default -> throw new IllegalArgumentException(
                    "Invalid Card: " + bool
            );
        };
    }



}
