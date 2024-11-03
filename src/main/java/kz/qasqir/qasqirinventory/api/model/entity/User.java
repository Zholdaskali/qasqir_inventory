package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "t_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name", nullable = false)
    private String userName ;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "emailVerified", nullable = false)
    private boolean emailVerified = false;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "organization_id")
    private Long organizationId;

    public User() {

    }
    public User(String userName,
                String email,String password, Long organizationId) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.organizationId = organizationId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
