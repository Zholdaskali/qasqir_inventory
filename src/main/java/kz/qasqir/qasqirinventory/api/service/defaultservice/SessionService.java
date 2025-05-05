package kz.qasqir.qasqirinventory.api.service.defaultservice;

import kz.qasqir.qasqirinventory.api.exception.SessionNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.SessionRepository;
import kz.qasqir.qasqirinventory.api.util.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final TokenGenerator tokenGenerator;
    private final UserService userService;

    @Autowired
    public SessionService(SessionRepository sessionRepository,
                          TokenGenerator tokenGenerator,
                          UserService userService)
    {
        this.sessionRepository = sessionRepository;
        this.tokenGenerator = tokenGenerator;
        this.userService = userService;
    }

    @Transactional
    public Session generateForUser(Long userId) {
        Session session = new Session();
        String token = tokenGenerator.generate();
        session.setToken(token);
        session.setUserId(userId);
        session.setExpiration(LocalDateTime.now().plusDays(1));
        return sessionRepository.save(session);
    }

    public User getTokenForUser(String token) {
        return sessionRepository.findByToken(token)
                .map(session -> userService.getByUserId(session.getUserId()))
                .orElseThrow(() -> {
                    System.out.println("Сессия не найдена для токена: " + token);
                    return new SessionNotFoundException();
                });
    }


    @Transactional
    public boolean invalidate(String token) {
        return sessionRepository.deleteByToken(token) > 0;
    }

    public void manageCountSession(Long userId) {
        List<Session> activeSessions = sessionRepository.findByUserId(userId);
        int maxCountActiveSessions = 2;

        if(activeSessions.size() > maxCountActiveSessions) {
            activeSessions.stream().min(Comparator.comparing(Session::getExpiration)).ifPresent(olderSession -> sessionRepository.delete(olderSession));
        }
    }

    public Session getSessionByToken(String token) {
        return sessionRepository.findByToken(token)
                .orElseThrow(SessionNotFoundException::new);
    }


}
