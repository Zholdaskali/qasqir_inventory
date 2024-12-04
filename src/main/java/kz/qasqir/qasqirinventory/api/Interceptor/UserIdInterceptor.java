package kz.qasqir.qasqirinventory.api.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
    public class UserIdInterceptor implements HandlerInterceptor {
    private final SessionService sessionService;

    public UserIdInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("UserIdInterceptor");
        String userIdFromSession = sessionService.getSessionByToken(request.getHeader("Auth-token")).getUserId().toString();
        String userIdFromRequest = request.getParameter("userId");
        if (!(userIdFromRequest == null)) {
            if (!userIdFromSession.equals(userIdFromRequest)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("Доступ запрещен: userId не совпадают");
                return false;
            }
        }
        return true;
    }
}
