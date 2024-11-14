package kz.qasqir.qasqirinventory.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import kz.qasqir.qasqirinventory.api.model.request.UpdateUserRequest;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.MailVerificationCheckRequest;
import kz.qasqir.qasqirinventory.api.model.request.MailVerificationSendRequest;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetInviteUserRequest;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetUserRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final MailVerificationService mailVerificationService;
    private final SessionService sessionService;

    @Autowired
    public UserController(UserService userService, PasswordResetService passwordResetService, MailVerificationService mailVerificationService, SessionService sessionService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.mailVerificationService = mailVerificationService;
        this.sessionService = sessionService;
    }

    @GetMapping("/profile")
    public UserDTO getProfile(@RequestHeader("auth-token") String token) {
        User user = sessionService.getTokenForUser(token);
        return userService.getUserProfileByUserId(user.getId());
    }

    @PutMapping("/profile/{userId}")
    public MessageResponse<UserDTO> resetUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return MessageResponse.of(userService.updateUser(updateUserRequest));
    }

    @PutMapping("/password/reset-invite")
    public MessageResponse<String> editPasswordInviteUser(HttpServletRequest request, @RequestBody PasswordResetInviteUserRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editPasswordInviteUser(request, passwordResetRequest));
    }

    @PutMapping("/password/reset/{userId}")
    public MessageResponse<String> editPasswordUser(HttpServletRequest request, @RequestBody PasswordResetUserRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editPasswordUser(request, passwordResetRequest));
    }

    @PostMapping("/email/generate")
    public boolean generateCode(@RequestBody MailVerificationSendRequest mailVerificationSendRequest) {
        return mailVerificationService.generate(mailVerificationSendRequest);
    }

    @PostMapping("/email/verify")
    public boolean verify(@RequestBody MailVerificationCheckRequest mailVerificationCheckRequest) {
        return mailVerificationService.verify(mailVerificationCheckRequest);
    }
}
