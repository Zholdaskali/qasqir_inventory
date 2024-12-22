package kz.qasqir.qasqirinventory.api.model.request;

import java.math.BigDecimal;

public class ReturnRequest {
    private String returnType; // Тип возврата (например, "Возврат поставщику", "Возврат от покупателя")
    private Long relatedDocumentId; // ID связанного документа (например, накладная или инвойс)
    private Long nomenclatureId; // ID товара
    private BigDecimal quantity; // Количество возвращаемого товара
    private String reason; // Причина возврата

    // Конструктор по умолчанию
    public ReturnRequest() {}

    // Полный конструктор
    public ReturnRequest(String returnType, Long relatedDocumentId, Long nomenclatureId, BigDecimal quantity, String reason) {
        this.returnType = returnType;
        this.relatedDocumentId = relatedDocumentId;
        this.nomenclatureId = nomenclatureId;
        this.quantity = quantity;
        this.reason = reason;
    }

    // Геттеры и сеттеры
    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Long getRelatedDocumentId() {
        return relatedDocumentId;
    }

    public void setRelatedDocumentId(Long relatedDocumentId) {
        this.relatedDocumentId = relatedDocumentId;
    }

    public Long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(Long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ReturnRequest{" +
                "returnType='" + returnType + '\'' +
                ", relatedDocumentId=" + relatedDocumentId +
                ", nomenclatureId=" + nomenclatureId +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                '}';
    }
}

