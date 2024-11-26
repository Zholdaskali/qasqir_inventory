package kz.qasqir.qasqirinventory.api.exception;

public class InviteNotFoundException extends RuntimeException {
    public InviteNotFoundException() {
        super("Приглашение не найдено. Пожалуйста, убедитесь, что используете правильную ссылку или запросите новое приглашение.");
    }
}

