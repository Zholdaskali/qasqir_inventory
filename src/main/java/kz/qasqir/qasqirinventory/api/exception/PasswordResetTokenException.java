package kz.qasqir.qasqirinventory.api.exception;

public class PasswordResetTokenException extends RuntimeException {
    public PasswordResetTokenException() {
        super("Токен для восстановление пароля не найдена");
    }

    public PasswordResetTokenException(String message) {
        super(message);
    }
}
