package kz.qasqir.qasqirinventory.api.exception;

public class EmailIsAlreadyRegisteredException extends RuntimeException {
    public EmailIsAlreadyRegisteredException() {
        super("Этот email уже зарегистрирован. Пожалуйста, используйте другой email или войдите в свою учетную запись.");
    }
}

