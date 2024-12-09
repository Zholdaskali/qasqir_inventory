package kz.qasqir.qasqirinventory.api.model.dto;

import java.sql.Timestamp;

public class LoginLogDTO {
    private Long loginLogId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Timestamp timestamp;

    public LoginLogDTO(Long loginLogId, Long userId, String userName, String userEmail, Timestamp timestamp) {
        this.loginLogId = loginLogId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
