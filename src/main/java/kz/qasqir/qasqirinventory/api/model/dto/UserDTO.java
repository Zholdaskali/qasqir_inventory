package kz.qasqir.qasqirinventory.api.model.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserDTO {
    private Long userId;
    private String userName;
    private String email;
    private String userNumber;
    private LocalDateTime registrationDate;
    private String imagePath;
    private boolean emailVerified;
    private List<String> userRoles; // Множественные роли

    public UserDTO(Long userId, String userName, String email, String userNumber, boolean emailVerified, List<String> userRoles, LocalDateTime registrationDate, String imagePath) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userNumber = userNumber;
        this.emailVerified = emailVerified;
        this.userRoles = userRoles;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
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

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
