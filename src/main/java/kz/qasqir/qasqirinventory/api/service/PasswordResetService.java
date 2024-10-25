package kz.qasqir.qasqirinventory.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.InvalidPasswordException;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PasswordResetService {
    @Autowired
    private InviteService inviteService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Transactional
    public String editPasswordUser(HttpServletRequest request,
                                            @RequestBody PasswordResetRequest passwordResetRequest
    ) {
        User editUser = inviteService.getTokenForUser(request.getParameter("Invite-token"));
        String hashNewPassword = passwordEncoder.hash(passwordResetRequest.getNewPassword());
        editUser.setPassword(hashNewPassword);
        userService.saveUser(editUser);
        inviteService.invalidate(request.getHeader("Auth-token"));
        return "Пароль успешно изменен";
    }
}
