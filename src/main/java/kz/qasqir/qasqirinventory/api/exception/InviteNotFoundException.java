package kz.qasqir.qasqirinventory.api.exception;

public class InviteNotFoundException extends RuntimeException{
    public InviteNotFoundException() {
        super("Invite not found");
    }
}
