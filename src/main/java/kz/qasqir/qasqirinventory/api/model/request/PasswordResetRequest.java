package kz.qasqir.qasqirinventory.api.model.request;

public class PasswordResetRequest {
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
