package kz.qasqir.qasqirinventory.api.model.dto;

import java.sql.Timestamp;

public class ExceptionLogDTO {
    private Long exceptionId;
    private String cause;
    private String message;
    private Timestamp timestamp;

    public ExceptionLogDTO(Long exceptionId, String cause, String message, Timestamp timestamp) {
        this.exceptionId = exceptionId;
        this.cause = cause;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Long getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Long exceptionId) {
        this.exceptionId = exceptionId;
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
