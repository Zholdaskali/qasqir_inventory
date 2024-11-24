package kz.qasqir.qasqirinventory.api.model.dto;

import java.sql.Timestamp;

public class ActionLogDTO {
    private Long actionLoId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String action;
    private String endpoint;
    private Timestamp timestamp;

    public ActionLogDTO(Long actionLoId, Long userId, String userName, String userEmail, String action, String endpoint, Timestamp timestamp) {
        this.actionLoId = actionLoId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.action = action;
        this.endpoint = endpoint;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getActionLoId() {
        return actionLoId;
    }

    public void setActionLoId(Long actionLoId) {
        this.actionLoId = actionLoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
