package kz.qasqir.qasqirinventory.api.Interceptor;

import kz.qasqir.qasqirinventory.api.exception.SessionHasExpiredException;
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
import java.util.Map;
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


    //roles
    @Value("${userRoles.warehouse_manager}")
    private String ROLE_WAREHOUSE_MANAGER;

    @Value("${userRoles.admin}")
    private String ROLE_ADMIN;

    @Value("${userRoles.employee}")
    private String ROLE_EMPLOYEE;

    @Value("${userRoles.storekeeper}")
    private String ROLE_STOREKEEPER;


    //api
    @Value("${api.path.warehouse_manager}")
    private String PATH_WAREHOUSE_MANAGER_API;

    @Value("${api.path.admin}")
    private String PATH_ADMIN_API;

    @Value("${api.path.employee}")
    private String PATH_EMPLOYEE_API;

    @Value("${api.path.storekeeper}")
    private String PATH_STOREKEEPER_API;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String authToken = request.getHeader("auth-token");

        if (authToken != null && !authToken.isEmpty()) {
            Session sessionOpt = validateSession(authToken);
            User user = userService.getByUserId(sessionOpt.getUserId());
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
        sendAccessDeniedResponse(response, "Доступ запрещен: недействительный токен");
        return false;
    }

    private Session validateSession(String authToken) throws IOException {
        System.out.println("validateSession");
        Session session = sessionService.getSessionByToken(authToken);
        if (LocalDateTime.now().isAfter(session.getExpiration())) {
            System.out.println("validateSession");
            sessionService.invalidate(session.getToken());
            throw new SessionHasExpiredException();
            }
        return session;
    }

    private final Map<String, String> rolePathMap = Map.of(
            PATH_WAREHOUSE_MANAGER_API, ROLE_WAREHOUSE_MANAGER,
            PATH_ADMIN_API, ROLE_ADMIN,
            PATH_EMPLOYEE_API, ROLE_EMPLOYEE,
            PATH_STOREKEEPER_API, ROLE_STOREKEEPER
    );

    private boolean hasAccess(String requestPath, List<Role> roles) {
        Set<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toSet());
        return rolePathMap.entrySet().stream()
                .anyMatch(entry -> requestPath.startsWith(entry.getKey()) && roleNames.contains(entry.getValue()));
    }


    private void sendAccessDeniedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}