package kz.qasqir.qasqirinventory.api.exception;

public class SessionHasExpiredException extends RuntimeException {
    public SessionHasExpiredException() {
        super ("Session has expired");
    }
}
