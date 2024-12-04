package kz.qasqir.qasqirinventory.api.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Пользователь не найден. Пожалуйста, проверьте правильность введенных данных.");
    }

    public UserNotFoundException(String message) {
        super("Пользователь не найден." + message);
    }
}

