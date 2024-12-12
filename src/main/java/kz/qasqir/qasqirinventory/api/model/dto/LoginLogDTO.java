package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class LoginLogDTO {
    private Long loginLogId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Timestamp timestamp;
}
