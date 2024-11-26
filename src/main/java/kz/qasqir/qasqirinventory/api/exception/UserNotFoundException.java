package kz.qasqir.qasqirinventory.api.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Пользователь не найден. Пожалуйста, проверьте правильность введенных данных.");
    }
}

