package kz.qasqir.qasqirinventory.api.model.request;

public class PasswordResetInviteUserRequest {
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
