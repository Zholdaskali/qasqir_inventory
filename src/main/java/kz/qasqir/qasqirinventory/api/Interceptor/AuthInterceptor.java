package kz.qasqir.qasqirinventory.api.Interceptor;

import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.RoleRepository;
import kz.qasqir.qasqirinventory.api.repository.SessionRepository;
import kz.qasqir.qasqirinventory.api.service.SessionService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private RoleRepository roleRepository;

    private static final String ROLE_COMPANY_ADMIN = "company_admin";
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final String ADMIN_API_PATH = "/api/v1/admin";
    private static final String SUPER_ADMIN_API_PATH = "/api/v1/super-admin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authToken = request.getHeader("Auth-token");

        if (authToken != null && !authToken.isEmpty()) {
            Optional<Session> sessionOpt = validateSession(authToken, response);
            if (sessionOpt.isPresent()) {
                User user = userService.getByUserId(sessionOpt.get().getUserId());
                List<Role> roles = roleRepository.getAllForUserId(user.getId());
                request.setAttribute("user", user);
                request.setAttribute("roles", roles);

                if (hasAccess(request.getRequestURI(), roles)) {
                    System.out.println(request.getRequestURI());
                    return true;
                }
                sendAccessDeniedResponse(response, "Access denied: insufficient permissions");
                return false;
            }
        }

        sendAccessDeniedResponse(response, "Access denied: invalid token");
        return false;
    }

    private Optional<Session> validateSession(String authToken, HttpServletResponse response) throws IOException {
        Optional<Session> sessionOpt = sessionRepository.findByToken(authToken);
        if (sessionOpt.isPresent()) {
            if (LocalDateTime.now().isAfter(sessionOpt.get().getExpiration())) {
                sessionService.invalidate(sessionOpt.get().getToken());
                sendAccessDeniedResponse(response, "Access denied: session expired");
                return Optional.empty();
            }
        }
        return sessionOpt;
    }

    private boolean hasAccess(String requestPath, List<Role> roles) {
        if (requestPath.startsWith(ADMIN_API_PATH)) {
            return roles.stream().anyMatch(role -> ROLE_COMPANY_ADMIN.equals(role.getRoleName()));
        } else if (requestPath.startsWith(SUPER_ADMIN_API_PATH)) {
            return roles.stream().anyMatch(role -> ROLE_SUPER_ADMIN.equals(role.getRoleName()));
        }
        return true;
    }

    private void sendAccessDeniedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}


