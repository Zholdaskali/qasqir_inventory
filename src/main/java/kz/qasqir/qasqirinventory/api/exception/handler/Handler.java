package kz.qasqir.qasqirinventory.api.exception.handler;

import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.log.ExceptionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class Handler {
    private final ExceptionLogService exceptionLogService;

    @Autowired
    public Handler(ExceptionLogService exceptionLogService) {
        this.exceptionLogService = exceptionLogService;
    }

    @ExceptionHandler(RuntimeException.class)
    public  ResponseEntity<MessageResponse<String>> handleGeneralException(RuntimeException ex) {
        exceptionLogService.save("Ошибка сервера: ", ex.getMessage());
        MessageResponse<String> response = MessageResponse.empty("Ошибка сервера: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
