package kz.qasqir.qasqirinventory.api.exception;

public class AuthenticationErrorException extends RuntimeException {
    public AuthenticationErrorException() {
        super("Authentication error");
    }
}
