package kz.qasqir.qasqirinventory.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.InvalidPasswordException;
import kz.qasqir.qasqirinventory.api.exception.InviteHasExpiredException;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetInviteUserRequest;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetUserRequest;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PasswordResetService {
    @Autowired
    private InviteService inviteService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Transactional
    public String editPasswordInviteUser(HttpServletRequest request,
                                            @RequestBody PasswordResetInviteUserRequest passwordResetRequest
    ) {
        String inviteToken = request.getParameter("Invite-token");
        if (inviteService.getByToken(inviteToken).isPresent()) {
            User editUser = inviteService.getTokenForUser(request.getParameter("Invite-token"));
            String hashNewPassword = passwordEncoder.hash(passwordResetRequest.getNewPassword());
            editUser.setPassword(hashNewPassword);
            userService.saveUser(editUser);
            inviteService.invalidate(request.getParameter("Invite-token"));
            return "Пароль успешно изменен";
        }
        else {
            throw new InviteHasExpiredException();
        }
    }

    @Transactional
    public String editPasswordUser(HttpServletRequest request,
                                   @RequestBody PasswordResetUserRequest passwordResetRequest
    ) {
        User editUser = sessionService.getTokenForUser(request.getHeader("Auth-token"));
        if(passwordEncoder.check(passwordResetRequest.getOldPassword(), editUser.getPassword())) {
            String hashNewPassword = passwordEncoder.hash(passwordResetRequest.getNewPassword());
            editUser.setPassword(hashNewPassword);
            userService.saveUser(editUser);
            sessionService.invalidate(request.getHeader("Auth-token"));
            return "Пароль успешно изменен";
        }
        throw new InvalidPasswordException();
    }
}
