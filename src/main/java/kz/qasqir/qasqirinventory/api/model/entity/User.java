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
    @Column(name = "phone_number")
    private String userNumber;
    @Column(name = "emailVerified", nullable = false)
    private boolean emailVerified = false;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "image_id")
    private Long imageId;

    public User() {

    }
    public User(String userName,
                String email,
                String userNumber,
                String password)
    {
        this.userName = userName;
        this.email = email;
        this.userNumber = userNumber;
        this.password = password;
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

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long organizationId) {
        this.imageId = imageId;
    }
}
