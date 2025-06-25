package br.com.rhribeiro25.virtual_card_platform.shared.Exception;

import br.com.rhribeiro25.virtual_card_platform.shared.utils.MessageUtil;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException() {
        super(MessageUtil.getMessage("database.internal.server.error"));

    }
}

