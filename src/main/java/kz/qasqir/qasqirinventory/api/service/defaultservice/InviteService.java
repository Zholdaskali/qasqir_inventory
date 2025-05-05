package kz.qasqir.qasqirinventory.api.service.defaultservice;

import kz.qasqir.qasqirinventory.api.exception.InviteNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.InviteUserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.InviteRepository;
import kz.qasqir.qasqirinventory.api.util.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        String token = tokenGenerator.generate();
        String link = path + "?Invite-token=" + token;

        Invite invite = new Invite(token, link, LocalDateTime.now().plusDays(3), LocalDateTime.now(), userId);
        return inviteRepository.save(invite);
    }

    public User getTokenForUser(String token) {
        return inviteRepository.findByToken(token)
                .map(invite -> userService.getByUserId(invite.getUserId()))
                .orElseThrow(InviteNotFoundException::new);
    }

    public Invite getByToken(String token) {
        return inviteRepository.findByToken(token).orElseThrow(InviteNotFoundException::new);
    }

    @Transactional
    public boolean invalidate(String token) {
        return inviteRepository.deleteByToken(token) > 0;
    }

    public List<InviteUserDTO> getInviteIdAndUserNameAndEmail() {
        return inviteRepository.findInviteIdAndUserNameAndEmail();
    }

    public boolean deleteInvite(Long inviteId) {
        return inviteRepository.deleteInviteById(inviteId) > 1;
    }
}
