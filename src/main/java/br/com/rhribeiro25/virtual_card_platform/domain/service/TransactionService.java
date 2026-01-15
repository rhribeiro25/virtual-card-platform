package br.com.rhribeiro25.virtual_card_platform.domain.service;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.TransactionType;
import org.springframework.stereotype.Service;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.StringUtils.normalize;

@Service
public class TransactionService {
    /*******************************************************************************************************************
    SPRING BATCH METHODS
    ********************************************************************************************************************/
    public TransactionType mapType(String txKind) {

        return switch (normalize(txKind)) {
            case "P" -> TransactionType.SPEND;
            case "C" -> TransactionType.TOPUP;
            case "T" -> TransactionType.TRANSFER;
            default -> throw new IllegalArgumentException(
                    "Invalid transaction type: " + txKind
            );
        };
    }
}
