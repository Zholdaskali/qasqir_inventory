package kz.qasqir.qasqirinventory.api.exception;

public class ExceptionLogSaveFailedException extends RuntimeException {
    public ExceptionLogSaveFailedException() {
        super("Не удалось сохранить лог исключений. Попробуйте позже.");
    }
}
