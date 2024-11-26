package kz.qasqir.qasqirinventory.api.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.service.InviteService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InviteInterceptor implements HandlerInterceptor {
    private final InviteService inviteService;

    public InviteInterceptor(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String inviteToken = request.getParameter("Invite-token");

        if ( inviteToken == null|| !getCurrentInviteId(inviteToken)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Access denied: invalid or missing invitation token\"}");
            return false;
        }
        return true;
    }

    private Boolean getCurrentInviteId(String token) {
        return token != null && inviteService.getByToken(token).isPresent();
    }
}



