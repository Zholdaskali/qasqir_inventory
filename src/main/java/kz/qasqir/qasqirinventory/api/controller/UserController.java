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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final MailVerificationService mailVerificationService;
    private final SessionService sessionService;
    private final ImageService imageService;

    @Autowired
    public UserController(UserService userService, PasswordResetService passwordResetService, MailVerificationService mailVerificationService, SessionService sessionService, ImageService imageService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.mailVerificationService = mailVerificationService;
        this.sessionService = sessionService;
        this.imageService = imageService;
    }

    @GetMapping("/profile")
    public UserDTO getProfile(@RequestHeader("auth-token") String token) {
        User user = sessionService.getTokenForUser(token);
        return userService.getUserProfileByUserId(user.getId());
    }

    @PutMapping("/profile/{userId}")
    public MessageResponse<UserDTO> resetUser(@PathVariable Long userId, @RequestBody UpdateUserRequest updateUserRequest) {
        return MessageResponse.of(userService.updateUser(updateUserRequest, userId));
    }

    @PutMapping("/password/reset-invite")
    public MessageResponse<String> editPasswordInviteUser(
            HttpServletRequest request,
            @RequestBody PasswordResetInviteUserRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editPasswordInviteUser(request, passwordResetRequest));
    }

    @PutMapping("/password/reset/{userId}")
    public MessageResponse<String> editPasswordUser(
            HttpServletRequest request,
            @RequestBody PasswordResetUserRequest passwordResetRequest) {
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

    @PostMapping("/{userId}/image")
    public MessageResponse<String> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
            return imageService.addImage(userId, file);
    }

    @DeleteMapping("/{userId}")
    public MessageResponse<Boolean> deleteUser(@PathVariable Long userId) {
        return MessageResponse.of(userService.deleteUserById(userId));
    }
}
