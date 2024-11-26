package kz.qasqir.qasqirinventory.api.exception;

public class InviteHasExpiredException extends RuntimeException {
    public InviteHasExpiredException() {
        super("Срок действия приглашения истек. Пожалуйста, запросите новое приглашение.");
    }
}

