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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authToken = request.getHeader("Auth-token");
        Optional<Session> session = sessionRepository.findByToken(authToken);
        if (session.isPresent()) {
            System.out.println("Session found: " + session.get());
            if (LocalDateTime.now().isAfter(session.get().getExpiration())) {
                sessionService.invalidate(session.get().getToken());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Access denied: session expired\"}");
                return false;
            }

            User user = userService.getByUserId(session.get().getUserId());
            List<Role> roles = roleRepository.getAllForUserId(user.getId());
            request.setAttribute("user", user);
            request.setAttribute("roles", roles);
        }
        else {
            System.out.println("No session found for token: " + authToken);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Access denied: invalid token\"}");
            return false;
        }

        return true;
    }


}
