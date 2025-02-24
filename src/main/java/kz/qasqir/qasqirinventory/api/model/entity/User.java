package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "t_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name", nullable = false)
    private String userName ;
    @Column(name = "email", nullable = false)
    @Email(message="Enter valid Email Id.")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    @Column(name = "emailVerified", nullable = false)
    private boolean emailVerified = false;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "image_id")
    private Long imageId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "t_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    public User() {

    }
    public User(String userName,
                String email,
                String userNumber,
                String password)
    {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = userNumber;
        this.password = password;
        this.registrationDate = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
    }
}
