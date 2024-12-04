package kz.qasqir.qasqirinventory.api.exception;

public class FailedToAddImageException extends RuntimeException {
    public FailedToAddImageException(String message) {
        super("Ошибка при сохранении изображения: " + message);
    }
}

