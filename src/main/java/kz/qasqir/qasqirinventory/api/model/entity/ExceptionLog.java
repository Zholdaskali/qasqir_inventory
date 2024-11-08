package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_exception_log")
public class ExceptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cause", nullable = false)
    private String cause;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Конструктор по умолчанию для JPA
    protected ExceptionLog() {}

    // Приватный конструктор для фабричного метода
    private ExceptionLog(String cause, String message) {
        this.cause = cause;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Фабричный метод для удобного создания экземпляра
    public static ExceptionLog of(String cause, String message) {
        return new ExceptionLog(cause, message);
    }

    // Геттеры и сеттеры (если нужно)
    public Long getId() {
        return id;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

