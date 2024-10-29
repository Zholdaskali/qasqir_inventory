package kz.qasqir.qasqirinventory.api.exception;

public class EmailIsAlreadyRegisteredException extends RuntimeException{
    public EmailIsAlreadyRegisteredException() {
        super("Email Уже зарегистрирован");
    }
}
