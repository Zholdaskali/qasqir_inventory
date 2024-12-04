package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Получить профиль пользователя",
            description = "Возвращает профиль пользователя по его токену"
    )
    @GetMapping("/profile")
    public UserDTO getProfile(@RequestHeader("auth-token") String token) {
        User user = sessionService.getTokenForUser(token);
        return userService.getUserProfileByUserId(user.getId());
    }

    @Operation(
            summary = "Изменение профиля(данных) пользователя",
            description = "Возвращает измененный профиль пользователя"
    )
    @PutMapping("/profile/{userId}")
    public MessageResponse<UserDTO> resetUser(@PathVariable Long userId, @RequestBody UpdateUserRequest updateUserRequest) {
        return MessageResponse.of(userService.updateUser(updateUserRequest, userId));
    }

    @Operation(
            summary = "Изменение пароля приглашенного пользователя",
            description = "Возвращает ответ изменения пароля"
    )
    @PutMapping("/password/reset-invite")
    public MessageResponse<String> editPasswordInviteUser(
            @RequestParam("Invite-token") String token,
            @RequestBody PasswordResetInviteUserRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editPasswordInviteUser(token, passwordResetRequest));
    }

    @Operation(
            summary = "Изменение пароля пользователя",
            description = "Возвращает ответ изменения пароля"
    )
    @PutMapping("/password/reset/{userId}")
    public MessageResponse<String> editPasswordUser(
            @RequestBody PasswordResetUserRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editPasswordUser(passwordResetRequest));
    }

    @Operation(
            summary = "Генерация кода для подтверждения email",
            description = "Возвращает ответ подтверждения email"
    )
    @PostMapping("/email/generate")
    public boolean generateCode(@RequestBody MailVerificationSendRequest mailVerificationSendRequest) {
        return mailVerificationService.generate(mailVerificationSendRequest);
    }

    @Operation(
            summary = "Проверка кода подтверждения email",
            description = "Возвращает ответ совпадения email"
    )
    @PostMapping("/email/verify")
    public boolean verify(@RequestBody MailVerificationCheckRequest mailVerificationCheckRequest) {
        return mailVerificationService.verify(mailVerificationCheckRequest);
    }

    @Operation(
            summary = "Добавление изображения для пользователя",
            description = "Возвращает ответ добавление изображения"
    )
    @PostMapping("/{userId}/image")
    public MessageResponse<String> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
            return imageService.addImage(userId, file);
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Возвращает ответ удаление пользователя"
    )
    @DeleteMapping("/{userId}")
    public MessageResponse<Boolean> deleteUser(@PathVariable Long userId) {
        return MessageResponse.of(userService.deleteUserById(userId));
    }
}
