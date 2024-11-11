package kz.qasqir.qasqirinventory.api.Interceptor.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.repository.ActionLogRepository;
import kz.qasqir.qasqirinventory.api.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@Component
public class ActionLogInterceptor implements HandlerInterceptor {

    private final ActionLogRepository actionLogRepository;
    private final SessionService sessionService; // или любая другая логика для получения текущего пользователя

    public ActionLogInterceptor(ActionLogRepository actionLogRepository, SessionService sessionService) {
        this.actionLogRepository = actionLogRepository;
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String methodName = handler.toString();
        String endpoint = request.getRequestURI();
        Long userId = getCurrentUserId(request.getHeader("Auth-token"));
        String action = methodName;
        String data = getRequestData(request);
        saveActionLog(userId, action, endpoint, data);
        return true;
    }

    private Long getCurrentUserId(String token) {
        return sessionService.getTokenForUser(token).getId();
    }

    private String getRequestData(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            return data.toString();
        } catch (IOException e) {
            return "";
        }
    }

    // Сохранение записи в лог
    private void saveActionLog(Long userId, String action, String endpoint, String data) {
        ActionLog log = new ActionLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEndpoint(endpoint);
        log.setData(data);
        log.setTimestamp(Timestamp.from(Instant.now()));
        actionLogRepository.save(log);
    }
}


