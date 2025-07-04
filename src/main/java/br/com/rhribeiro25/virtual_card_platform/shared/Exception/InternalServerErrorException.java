package br.com.rhribeiro25.virtual_card_platform.shared.Exception;

import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtils;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BusinessException {
    public InternalServerErrorException() {
        super(MessageUtils.getMessage("database.internal.server.error"), HttpStatus.INTERNAL_SERVER_ERROR);

    }
}

