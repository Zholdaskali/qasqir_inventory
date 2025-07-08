package kz.qasqir.qasqirinventory.api.model.enums;

public enum OperationType {
    SALES("Продажа"),
    INCOMING("Поступление"),
    TRANSFER("Перемещение"),
    WRITE_OFF("Списание"),
    RETURN("Возврат"),
    PRODUCTION("Производство"),
    ONE_C_SALES("1С-Продажа");

    private final String label;

    OperationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static OperationType fromString(String value) {
        return switch (value) {
            case "1C-SALES" -> ONE_C_SALES;
            case "SALES" -> SALES;
            case "INCOMING" -> INCOMING;
            case "TRANSFER" -> TRANSFER;
            case "WRITE-OFF" -> WRITE_OFF;
            case "RETURN" -> RETURN;
            case "PRODUCTION" -> PRODUCTION;
            default -> throw new IllegalArgumentException("Unknown operation type: " + value);
        };
    }
}
