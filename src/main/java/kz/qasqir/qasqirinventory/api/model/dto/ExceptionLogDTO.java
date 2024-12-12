package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ExceptionLogDTO {
    private Long exceptionId;
    private String cause;
    private String message;
    private Timestamp timestamp;
}
