package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.request.OrganizationResetRequest;
import kz.qasqir.qasqirinventory.api.model.request.RegisterInviteRequest;
import kz.qasqir.qasqirinventory.api.model.request.UserRoleResetRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ActionLogService actionLogService;
    private final ExceptionLogService exceptionLogService;
    private final LoginLogService loginLogService;
    private final InviteService inviteService;
    private final OrganizationService organizationService;

    @Autowired
    public AdminController(AuthenticationService authenticationService, UserService userService, ActionLogService actionLogService, ExceptionLogService exceptionLogService, LoginLogService loginLogService, InviteService inviteService, OrganizationService organizationService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.exceptionLogService = exceptionLogService;
        this.actionLogService = actionLogService;
        this.loginLogService = loginLogService;
        this.inviteService = inviteService;
        this.organizationService = organizationService;
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Возвращает ответ удаление пользователя"
    )
    @DeleteMapping("/user/{userId}")
    public MessageResponse<Boolean> deleteUser(@PathVariable Long userId) {
        return MessageResponse.of(userService.deleteUserById(userId));
    }

    @Operation(
            summary = "Вывод всех пользователей системы",
            description = "Возвращает список пользователей с данными"
    )
    @GetMapping("/user")
    public MessageResponse<List<UserDTO>> getUserAll() {
        return MessageResponse.of(userService.getUserAll());
    }

    @Operation(
            summary = "Изменение роли пользователя",
            description = "Данные измененного пользователя"
    )
    @PutMapping("/user/{userId}/role")
    public MessageResponse<UserDTO> resetRole(@PathVariable("userId") Long userId, @RequestBody UserRoleResetRequest userRoleResetRequest) {
        return MessageResponse.of(userService.updateRole(userId, userRoleResetRequest));
    }

    @Operation(
            summary = "Создание приглашения пользователя",
            description = "Возвращает сообщение о создании приглашения"
    )
    @PostMapping("/invite")
    public MessageResponse<String> inviteCreate(@RequestBody RegisterInviteRequest registerInviteRequest) {
        return MessageResponse.empty(authenticationService.registerInvite(registerInviteRequest.getUserName(),registerInviteRequest.getEmail(),registerInviteRequest.getUserNumber() , registerInviteRequest.getPassword()));
    }

    @Operation(
            summary = "Вывод всех приглашенных пользователей системы",
            description = "Возвращает список приглашений с данными"
    )
    @GetMapping("/invites")
    public MessageResponse<List<InviteUserDTO>> getInviteAll() {
        return MessageResponse.of(inviteService.getInviteIdAndUserNameAndEmail());
    }

    @DeleteMapping("/invites/{inviteId}")
    public MessageResponse<Boolean> deleteInvite(
            @PathVariable("inviteId") Long inviteId
    ) {
        return MessageResponse.of(inviteService.deleteInvite(inviteId));
    }

    @Operation(
            summary = "Скачивание активностей пользователей в системе",
            description = "Возвращает список логов активностей пользователей в системе"
    )
    @GetMapping("/log/action-logs")
    public MessageResponse<List<ActionLogDTO>> getActionLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        return MessageResponse.of(actionLogService.getActionLogs(startDate, endDate));
    }

    @Operation(
            summary = "Скачивание логов ошибок системы",
            description = "Возвращает список логов ошибок системы"
    )
    @GetMapping("/log/exception-logs")
    public MessageResponse<List<ExceptionLogDTO>> getExceptionLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        return MessageResponse.of(exceptionLogService.getExceptionLogs(startDate, endDate));
    }

    @Operation(
            summary = "Скачивание логов входа в систему",
            description = "Возвращает список логов входа в систему"
    )
    @GetMapping("/log/login-logs")
    public MessageResponse<List<LoginLogDTO>> getLoginLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        return MessageResponse.of(loginLogService.getLoginLogs(startDate, endDate));
    }

    @Operation(
            summary = "Вывод всех организаций",
            description = "Возвращает список организаций"
    )
    @GetMapping("/organization")
    public MessageResponse<OrganizationDTO> getOrganization() {
        return MessageResponse.of(organizationService.getOrganization());
    }

    @Operation(
            summary = "Изменение параметров организации",
            description = "Возвращает измененную организацию"
    )
    @PutMapping("/organization")
    public MessageResponse<OrganizationDTO> resetOrganization(@RequestBody OrganizationResetRequest organizationResetRequest) {
        return MessageResponse.of(organizationService.resetOrganization(organizationResetRequest));
    }


//        {
//            "userName": "superAdmin1@gmail.com",
//            "password": "TorgutOzalaqasqirAdminkz02"
//        }

//        {
//            "userName": "erkebulanzholdaskali@gmail.com",
//            "password": "ErkebulanAdmin0404"
//        }

}
    