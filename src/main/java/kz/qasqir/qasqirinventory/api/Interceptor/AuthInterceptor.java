package kz.qasqir.qasqirinventory.api.Interceptor;

import kz.qasqir.qasqirinventory.api.exception.SessionHasExpiredException;
import kz.qasqir.qasqirinventory.api.exception.SessionNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.RoleRepository;
import kz.qasqir.qasqirinventory.api.service.SessionService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserService userService;
    private final SessionService sessionService;
    private final RoleRepository roleRepository;

    @Autowired
    public AuthInterceptor(RoleRepository roleRepository, UserService userService, SessionService sessionService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @Value("${userRoles.company_admin}")
    private String ROLE_COMPANY_ADMIN;

    @Value("${userRoles.super_admin}")
    private String ROLE_SUPER_ADMIN;

    @Value("${api.path.admin}")
    private String ADMIN_API_PATH;

    @Value("${api.path.super_admin}")
    private String SUPER_ADMIN_API_PATH;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String authToken = request.getHeader("auth-token");

        if (authToken != null && !authToken.isEmpty()) {
            Optional<Session> sessionOpt = validateSession(authToken);
            if (sessionOpt.isPresent()) {
                User user = userService.getByUserId(sessionOpt.get().getUserId());
                List<Role> roles = roleRepository.getAllForUserId(user.getId());
                request.setAttribute("user", user);
                request.setAttribute("roles", roles);

                if (hasAccess(request.getRequestURI(), roles)) {
                    System.out.println(request.getRequestURI());
                    return true;
                }
                sendAccessDeniedResponse(response, "Доступ запрещен: недостаточно прав");
                return false;
            }
        }

        sendAccessDeniedResponse(response, "Доступ запрещен: недействительный токен");
        return false;
    }

    private Optional<Session> validateSession(String authToken) throws IOException {
        Optional<Session> sessionOpt = sessionService.getSessionByToken(authToken);
        if (sessionOpt.isPresent()) {
            if (LocalDateTime.now().isAfter(sessionOpt.get().getExpiration())) {
                sessionService.invalidate(sessionOpt.get().getToken());
                throw new SessionHasExpiredException();
            }
            return sessionOpt;
        }
        throw new SessionNotFoundException();
    }


    private boolean hasAccess(String requestPath, List<Role> roles) {
        Set<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toSet());

        if (requestPath.startsWith(ADMIN_API_PATH) && roleNames.contains(ROLE_COMPANY_ADMIN)) {
            return true;
        } else if (requestPath.startsWith(SUPER_ADMIN_API_PATH) && roleNames.contains(ROLE_SUPER_ADMIN)) {
            return true;
        }
        return !requestPath.startsWith(ADMIN_API_PATH) && !requestPath.startsWith(SUPER_ADMIN_API_PATH);
    }

    private void sendAccessDeniedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}