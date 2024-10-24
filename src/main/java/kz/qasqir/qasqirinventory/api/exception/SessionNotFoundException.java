package kz.qasqir.qasqirinventory.api.exception;

public class SessionNotFoundException extends RuntimeException{
    public SessionNotFoundException() {
        super("Session not found");
    }
}
