package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "t_mail_verifications")
public class MailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "code", nullable = false)
    private String code;

    public MailVerification() {
    }

    public MailVerification(String email, String code) {
        this.email = email;
        this.code = code;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
