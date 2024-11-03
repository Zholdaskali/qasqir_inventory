package kz.qasqir.qasqirinventory.api.model.view;

import jakarta.persistence.*;

@Entity
@Table(name = "vw_user_profile")
public class UserProfileView {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String userEmail;

    @Column(name = "number")
    private String userNumber;

    @Column(name = "organization_name")
    private String organization;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "role_name")
    private String userRole;

    // Конструкторы
    public UserProfileView() {}

    public UserProfileView(Long userId, String userName, String userEmail,String userNumber, String organization, boolean emailVerified, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userNumber = userNumber;
        this.organization = organization;
        this.emailVerified = emailVerified;
        this.userRole = userRole;
    }


    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}

