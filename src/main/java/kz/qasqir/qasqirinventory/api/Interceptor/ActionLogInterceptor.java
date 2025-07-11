package kz.qasqir.qasqirinventory.api.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.repository.ActionLogRepository;
import kz.qasqir.qasqirinventory.api.service.auth.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class ActionLogInterceptor implements HandlerInterceptor {

    private final ActionLogRepository actionLogRepository;
    private final SessionService sessionService;

    public ActionLogInterceptor(ActionLogRepository actionLogRepository, SessionService sessionService) {
        this.actionLogRepository = actionLogRepository;
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String methodName = extractMethodName(handler.toString());
        String endpoint = request.getRequestURI();
        Long userId = getCurrentUserId(request.getHeader("Auth-token"));
        String action = methodName;
        saveActionLog(userId, action, endpoint);
        return true;
    }

    private Long getCurrentUserId(String token) {
        return sessionService.getTokenForUser(token).getId();
    }

    // Сохранение записи в лог
    private void saveActionLog(Long userId, String action, String endpoint) {
        ActionLog log = new ActionLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEndpoint(endpoint);
        log.setTimestamp(Timestamp.from(Instant.now()));

        actionLogRepository.save(log);
    }

    private String extractMethodName(String fullMethodName) {
        int hashIndex = fullMethodName.indexOf('#');
        if (hashIndex != -1) {
            int paramsIndex = fullMethodName.indexOf('(', hashIndex);
            if (paramsIndex != -1) {
                return fullMethodName.substring(hashIndex + 1, paramsIndex);
            }
        }
        return fullMethodName;
    }
}