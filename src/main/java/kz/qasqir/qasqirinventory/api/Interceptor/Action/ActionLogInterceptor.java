package kz.qasqir.qasqirinventory.api.Interceptor.Action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.repository.ActionLogRepository;
import kz.qasqir.qasqirinventory.api.service.SessionService;
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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String methodName = handler.toString();
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
}