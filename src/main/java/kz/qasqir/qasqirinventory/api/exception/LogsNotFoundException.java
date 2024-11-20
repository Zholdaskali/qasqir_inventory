package kz.qasqir.qasqirinventory.api.exception;

public class LogsNotFoundException extends RuntimeException {
    public LogsNotFoundException() {
        super("Логи не найдены");
    }
}
