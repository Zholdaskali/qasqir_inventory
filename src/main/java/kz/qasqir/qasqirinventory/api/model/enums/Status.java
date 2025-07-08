package kz.qasqir.qasqirinventory.api.model.enums;

public enum Status {
    IN_PROGRESS("IN_PROGRESS", "В процессе"),
    COMPLETED("COMPLETED", "Завершено"),
    ACTIVE("ACTIVE", "Активный"),
    ALLOWED("ALLOWED", "Разрешено");

    private final String code;   // системный код (для логики и БД)
    private final String label;  // красивое название для пользователя

    Status(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}

