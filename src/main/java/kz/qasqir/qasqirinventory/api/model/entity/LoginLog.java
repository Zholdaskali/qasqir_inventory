package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
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
    private LocalDateTime timestamp;

    // Конструктор по умолчанию для JPA
    protected LoginLog() {}

    // Приватный конструктор для фабрики
    private LoginLog(Long userId) {
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    // Фабричный метод для создания экземпляра
    public static LoginLog of(Long userId) {
        return new LoginLog(userId);
    }

    // Геттеры и сеттеры (если нужно)
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

