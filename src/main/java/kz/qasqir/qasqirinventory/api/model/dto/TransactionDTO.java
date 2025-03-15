package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String transactionType;
    private Long nomenclatureId;
    private String nomenclatureName;
    private BigDecimal quantity;
    private LocalDate date;
    private String createdBy;
    private LocalDateTime createdAt;
    private Long documentId;
}
