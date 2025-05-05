package kz.qasqir.qasqirinventory.api.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.model.entity.PasswordResetToken;
import kz.qasqir.qasqirinventory.api.service.defaultservice.PasswordRecoveryService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PasswordResetTokenInterceptor implements HandlerInterceptor {
    private final PasswordRecoveryService passwordRecoveryService;

    public PasswordResetTokenInterceptor(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenParam = request.getParameter("Password-reset-token");

        if (tokenParam == null || tokenParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Токен восстановления пароля отсутствует.");
            return false;
        }

        PasswordResetToken token = passwordRecoveryService.getPasswordTokenByToken(tokenParam);

        if (token == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Недействительный токен восстановления пароля.");
            return false;
        }

        if (token.isExpired()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Токен восстановления пароля истёк.");
            return false;
        }

        // Токен валиден
        return true;
    }

}
