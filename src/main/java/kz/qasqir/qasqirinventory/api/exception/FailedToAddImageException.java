package kz.qasqir.qasqirinventory.api.exception;

public class FailedToAddImageException extends RuntimeException {
    public FailedToAddImageException() {
        super("Ошибка при сохранении изображения. Пожалуйста, убедитесь, что файл изображения имеет правильный формат и размер.");
    }
}

