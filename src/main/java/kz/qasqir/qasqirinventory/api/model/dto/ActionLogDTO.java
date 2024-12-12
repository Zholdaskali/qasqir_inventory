package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ActionLogDTO {
    private Long actionLogId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String action;
    private String endpoint;
    private Timestamp timestamp;
}
