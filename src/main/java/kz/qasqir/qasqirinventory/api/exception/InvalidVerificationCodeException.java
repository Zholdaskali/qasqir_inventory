package kz.qasqir.qasqirinventory.api.exception;

public class InvalidVerificationCodeException extends RuntimeException{
    public InvalidVerificationCodeException() {
        super("Invalid verification code");
    }
}
