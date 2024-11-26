package kz.qasqir.qasqirinventory.api.exception;

public class LogsNotFoundException extends RuntimeException {
    public LogsNotFoundException() {
        super("Не удалось найти логи за указанный период. Пожалуйста, убедитесь, что выбранный интервал времени верен, и попробуйте снова.");
    }
}