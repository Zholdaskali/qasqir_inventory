package kz.qasqir.qasqirinventory.api.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Неверный проверочный код. Возможно, код устарел. Запросите новый код для продолжения.");
    }
}

