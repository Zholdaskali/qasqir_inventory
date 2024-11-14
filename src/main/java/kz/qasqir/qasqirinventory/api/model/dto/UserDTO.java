package kz.qasqir.qasqirinventory.api.model.dto;

import java.util.List;

public class UserDTO {
    private Long userId;
    private String userName;
    private String email;
    private String userNumber;
    private boolean emailVerified;
    private String organization;
    private List<String> userRoles; // Множественные роли

    public UserDTO(Long userId, String userName, String email, String userNumber, String organization, boolean emailVerified, List<String> userRoles) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userNumber = userNumber;
        this.organization = organization;
        this.emailVerified = emailVerified;
        this.userRoles = userRoles;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }
}
