package kz.qasqir.qasqirinventory.api.model.dto;

import java.sql.Timestamp;

public class LoginLogDTO {
    private Long loginLogId;
    private Long userId;
    private String userName;
    private Timestamp timestamp;

    public LoginLogDTO(Long loginLogId, Long userId, String userName, Timestamp timestamp) {
        this.loginLogId = loginLogId;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public Long getLoginLogId() {
        return loginLogId;
    }

    public void setLoginLogId(Long loginLogId) {
        this.loginLogId = loginLogId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
