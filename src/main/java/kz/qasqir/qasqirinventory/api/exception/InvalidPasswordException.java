package kz.qasqir.qasqirinventory.api.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Неверный пароль");
    }
}
