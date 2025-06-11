package br.com.rhribeiro25.virtual_card_platform.shared.Exception;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(String message) {
        super(message);

    }
}