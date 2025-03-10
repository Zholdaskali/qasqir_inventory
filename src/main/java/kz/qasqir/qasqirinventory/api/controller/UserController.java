package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryItemDTO;
import kz.qasqir.qasqirinventory.api.model.dto.OrganizationDTO;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final MailVerificationService mailVerificationService;
    private final SessionService sessionService;
    private final ImageService imageService;
    private final PasswordRecoveryService passwordRecoveryService;
    private final OrganizationService organizationService;
    private final InventoryService inventoryService;

    @Autowired
    public UserController(UserService userService, PasswordResetService passwordResetService, MailVerificationService mailVerificationService, SessionService sessionService, ImageService imageService, PasswordRecoveryService passwordRecoveryService, OrganizationService organizationService, InventoryService inventoryService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.mailVerificationService = mailVerificationService;
        this.sessionService = sessionService;
        this.imageService = imageService;
        this.passwordRecoveryService = passwordRecoveryService;
        this.organizationService = organizationService;
        this.inventoryService = inventoryService;
    }

    @Operation(
            summary = "Получить профиль пользователя",
            description = "Возвращает профиль пользователя по его токену"
    )
    @GetMapping("/profile")
    public MessageResponse<UserDTO> getProfile(@RequestHeader("auth-token") String token) {
        User user = sessionService.getTokenForUser(token);
        return MessageResponse.of(userService.getUserProfileByUserId(user.getId()));
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
            summary = "Забыли пароль",
            description = "Возвращает ответ генерации пароля"
    )
    @PostMapping("password/recovery")
    public MessageResponse<String> recoveryPassword(
            @RequestBody RecoveryPasswordRequest recoveryPasswordRequest
            ) {
        return MessageResponse.empty(passwordRecoveryService.generate(recoveryPasswordRequest.getEmail()));
    }

    @Operation(
            summary = "Проверяет токен на валидность и меняет пароль",
            description = "Возвращает ответ изменения пароля"
    )
    @PutMapping("/password/reset")
    public MessageResponse<String> resetPassword(
            @RequestParam("Password-reset-token") String token,
            @RequestBody PasswordRecoveryRequest passwordRecoveryRequest
            ) {
        return MessageResponse.empty(passwordRecoveryService.passwordRecovery(token, passwordRecoveryRequest));
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

    @Operation(
            summary = "Вывод всех организаций",
            description = "Возвращает список организаций"
    )
    @GetMapping("/organization")
    public MessageResponse<OrganizationDTO> getOrganization() {
        return MessageResponse.of(organizationService.getOrganization());
    }

    @GetMapping("/inventory/items")
    public MessageResponse<List<InventoryItemDTO>> getAllInventoryItems() {
        return MessageResponse.of(inventoryService.getAllInventoryItems());
    }

    @GetMapping("/inventory/items/{warehouseZoneId}")
    public MessageResponse<List<InventoryItemDTO>> getAllInventoryItemsByWarehouseZoneId(@PathVariable Long warehouseZoneId) {
        return MessageResponse.of(inventoryService.getAllInventoryItemsByWarehouseZoneId(warehouseZoneId));
    }
}
