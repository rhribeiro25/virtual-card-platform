package br.com.rhribeiro25.virtual_card_platform.shared.Exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
