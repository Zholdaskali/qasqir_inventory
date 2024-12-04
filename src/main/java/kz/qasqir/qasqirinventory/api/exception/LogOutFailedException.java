package kz.qasqir.qasqirinventory.api.exception;

public class LogOutFailedException extends RuntimeException {
    public LogOutFailedException() {
        super("Не удалось выполнить выход, попробуйте позже.");
    }
}
