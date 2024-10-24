package kz.qasqir.qasqirinventory.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.PasswordResetService;
import kz.qasqir.qasqirinventory.api.service.SessionService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password")
public class PasswordResetController {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetService passwordResetService;

    @PutMapping("/reset")
    public MessageResponse<String> editPasswordUser(HttpServletRequest request, @RequestBody PasswordResetRequest passwordResetRequest) {
            return MessageResponse.empty(passwordResetService.editPasswordUser(request, passwordResetRequest));
    }
}
