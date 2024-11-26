package kz.qasqir.qasqirinventory.api.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Неверный пароль. Если вы забыли пароль, используйте функцию восстановления пароля или свяжитесь с поддержкой.");
    }
}

