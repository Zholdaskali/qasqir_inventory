package kz.qasqir.qasqirinventory.api.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import kz.qasqir.qasqirinventory.api.exception.InviteNotFoundException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.InviteRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InviteService {

    private final InviteRepository inviteRepository;
    private final TokenGenerator tokenGenerator;
    private final UserService userService;

    @Autowired
    public InviteService(InviteRepository inviteRepository,
                  TokenGenerator tokenGenerator,
                  UserService userService)
    {
        this.inviteRepository = inviteRepository;
        this.tokenGenerator = tokenGenerator;
        this.userService = userService;
    }


    @Transactional
    public Invite generate(String path,Long userId) {
        Invite invite = new Invite();
        String token = tokenGenerator.generate();
        String link = path + "?Invite-token=" + token;
        invite.setToken(token);
        invite.setLink(link);
        invite.setExpiration(LocalDateTime.now().plusDays(7));
        invite.setDateCreate(LocalDateTime.now());
        invite.setUserId(userId);
        return inviteRepository.save(invite);
    }

    public User getTokenForUser(String token) {
        return inviteRepository.findByToken(token)
                .map(invite -> userService.getByUserId(invite.getUserId()))
                .orElseThrow(InviteNotFoundException::new);
    }

    public Optional<Invite> getByToken(String token) {
        return inviteRepository.findByToken(token);
    }

    @Transactional
    public boolean invalidate(String token) {
        return inviteRepository.deleteByToken(token) > 0;
    }
}
