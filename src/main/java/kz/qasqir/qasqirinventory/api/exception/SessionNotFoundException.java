package kz.qasqir.qasqirinventory.api.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() {
        super("Сессия не найдена. Пожалуйста, войдите в систему, чтобы продолжить.");
    }
}

