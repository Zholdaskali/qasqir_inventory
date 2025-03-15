package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private String supplier;
    private String customer;
    private LocalDateTime createdAt;
    private Long createdBy;
}
