package kz.qasqir.qasqirinventory.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import kz.qasqir.qasqirinventory.api.model.dto.UpdateUserDTO;
import kz.qasqir.qasqirinventory.api.model.dto.UserProfileDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.MailVerificationCheckRequest;
import kz.qasqir.qasqirinventory.api.model.request.MailVerificationSendRequest;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetInviteUserRequest;
import kz.qasqir.qasqirinventory.api.model.request.PasswordResetUserRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.MailVerificationService;
import kz.qasqir.qasqirinventory.api.service.PasswordResetService;
import kz.qasqir.qasqirinventory.api.service.ProfileService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final ProfileService profileService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final MailVerificationService mailVerificationService;


    @Autowired
    public UserController(ProfileService profileService, UserService userService, PasswordResetService passwordResetService, MailVerificationService mailVerificationService) {
        this.profileService = profileService;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.mailVerificationService = mailVerificationService;
    }


    @GetMapping("/profile")
    public UserProfileDTO getProfile(@RequestHeader("auth-token") String token) {
        return profileService.getUserProfileByUserId(token);
    }

    @PutMapping("/profile/{userId}")
    public MessageResponse<User> resetUser(@PathVariable Long userId, @RequestBody UpdateUserDTO upDateUserDTO) {
        return MessageResponse.of(userService.updateUser(userId, upDateUserDTO));
    }

    @PutMapping("/password/reset-invite")
    public MessageResponse<String> editPasswordInviteUser(HttpServletRequest request, @RequestBody PasswordResetInviteUserRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editPasswordInviteUser(request, passwordResetRequest));
    }

    @PutMapping("/password/reset/{userId}")
    public MessageResponse<String> editPasswordUser(Long userId, @RequestBody PasswordResetUserRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editPasswordUser(userId, passwordResetRequest));
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
