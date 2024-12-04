package kz.qasqir.qasqirinventory.api.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
        super("Не удалось выполнить вход, попробуйте позже.");
    }
}
