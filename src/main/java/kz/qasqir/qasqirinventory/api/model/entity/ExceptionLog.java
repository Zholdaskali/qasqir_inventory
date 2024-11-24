package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;

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
    private Timestamp timestamp;

    // Конструктор по умолчанию для JPA
    protected ExceptionLog() {}

    // Приватный конструктор для фабричного метода
    private ExceptionLog(String cause, String message) {
        this.cause = cause;
        this.message = message;
        this.timestamp = Timestamp.from(Instant.now());
    }

    // Фабричный метод для удобного создания экземпляра
    public static ExceptionLog of(String cause, String message) {
        return new ExceptionLog(cause, message);
    }

    // Геттеры и сеттеры (если нужно)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

