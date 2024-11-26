package kz.qasqir.qasqirinventory.api.exception;

public class SessionHasExpiredException extends RuntimeException {
    public SessionHasExpiredException() {
        super("Срок действия сессии истек. Пожалуйста, войдите в систему снова.");
    }
}

