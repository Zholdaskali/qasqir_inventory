package kz.qasqir.qasqirinventory.api.exception;

public class NumberIsAlreadyRegisteredException extends RuntimeException {
    public NumberIsAlreadyRegisteredException() {
        super("Этот номер уже зарегистрирован. Пожалуйста, используйте другой номер или войдите в свою учетную запись.");
    }
}
