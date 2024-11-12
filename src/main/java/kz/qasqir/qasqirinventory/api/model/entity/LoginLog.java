package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_login_log")
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    // Конструктор по умолчанию для JPA
    protected LoginLog() {}

    // Приватный конструктор для фабрики
    private LoginLog(Long userId) {
        this.userId = userId;
        this.timestamp = Timestamp.from(Instant.now());
    }

    // Фабричный метод для создания экземпляра
    public static LoginLog of(Long userId) {
        return new LoginLog(userId);
    }

    // Геттеры и сеттеры (если нужно)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

