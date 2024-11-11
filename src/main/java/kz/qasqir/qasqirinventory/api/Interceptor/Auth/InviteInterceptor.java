package kz.qasqir.qasqirinventory.api.Interceptor.Auth;

import io.swagger.v3.core.util.ReferenceTypeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.service.InviteService;
import org.springframework.web.servlet.HandlerInterceptor;

public class InviteInterceptor implements HandlerInterceptor {
    private final InviteService inviteService;

    public InviteInterceptor(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return getCurrentInviteId(request.getParameter("Invite-token"));
    }

    public Boolean getCurrentInviteId(String token) {
        return inviteService.getByToken(token).isPresent();
    }
}
