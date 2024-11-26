package kz.qasqir.qasqirinventory.api.exception;

public class AuthenticationErrorException extends RuntimeException {
    public AuthenticationErrorException() {
        super("Ошибка аутентификации. Пожалуйста, проверьте правильность введенных данных и попробуйте снова.");
    }
}

