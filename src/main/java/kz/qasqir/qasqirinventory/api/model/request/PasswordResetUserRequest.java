package kz.qasqir.qasqirinventory.api.model.request;
public class PasswordResetUserRequest {

    private Long userId;
    private String oldPassword;
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}