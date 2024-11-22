package kz.qasqir.qasqirinventory.api.exception;

public class NumberIsAlreadyRegisteredException extends RuntimeException {
    public NumberIsAlreadyRegisteredException() {
        super("Номер уже зарегистрирован");
    }
}
