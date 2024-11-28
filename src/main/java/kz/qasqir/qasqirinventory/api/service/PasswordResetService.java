package kz.qasqir.qasqirinventory.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.InvalidPasswordException;
import kz.qasqir.qasqirinventory.api.exception.InviteHasExpiredException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetInviteUserRequest;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetUserRequest;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PasswordResetService {

    private final InviteService inviteService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PasswordResetService (InviteService inviteService,
                                 SessionService sessionService,
                                 UserService userService,
                                 PasswordEncoder passwordEncoder)
    {
        this.inviteService = inviteService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String editPasswordInviteUser(HttpServletRequest request,
                                            @RequestBody PasswordResetInviteUserRequest passwordResetRequest
    ) {
        String inviteToken = request.getParameter("Invite-token");
        if (inviteService.getByToken(inviteToken).isPresent()) {
            User editUser = inviteService.getTokenForUser(inviteToken);
            String hashNewPassword = passwordEncoder.hash(passwordResetRequest.getNewPassword());
            editUser.setPassword(hashNewPassword);
            editUser.setEmailVerified(true);
            userService.saveUser(editUser);
            inviteService.invalidate(inviteToken);
            return "Пароль успешно изменен";
        }
        else {
            throw new InviteHasExpiredException();
        }
    }

    @Transactional
    public String editPasswordUser(@RequestBody PasswordResetUserRequest passwordResetRequest
    ) {

        User editUser = userService.getByUserId(passwordResetRequest.getUserId());
        if(passwordEncoder.check(passwordResetRequest.getOldPassword(), editUser.getPassword())) {
            String hashNewPassword = passwordEncoder.hash(passwordResetRequest.getNewPassword());
            editUser.setPassword(hashNewPassword);
            userService.saveUser(editUser);
            return "Пароль успешно изменен";
        }
        throw new InvalidPasswordException();
    }

}
