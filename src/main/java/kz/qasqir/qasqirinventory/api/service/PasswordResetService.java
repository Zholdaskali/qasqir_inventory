package kz.qasqir.qasqirinventory.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.InvalidPasswordException;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetRequest;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PasswordResetService {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Transactional
    public String editPasswordUser(HttpServletRequest request,
                                   @RequestBody PasswordResetRequest passwordResetRequest
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
