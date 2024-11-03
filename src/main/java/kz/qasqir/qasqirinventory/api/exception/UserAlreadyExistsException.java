package kz.qasqir.qasqirinventory.api.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Пользователь уже существует");
    }
}
