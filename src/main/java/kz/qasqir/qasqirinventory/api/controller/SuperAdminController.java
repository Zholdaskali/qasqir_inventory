package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.InviteUserDTO;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.model.entity.ExceptionLog;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
import kz.qasqir.qasqirinventory.api.model.request.RegisterInviteRequest;
import kz.qasqir.qasqirinventory.api.model.request.RegisterRequest;
import kz.qasqir.qasqirinventory.api.model.request.UserRoleResetRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/super-admin")
public class SuperAdminController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ActionLogService actionLogService;
    private final ExceptionLogService exceptionLogService;
    private final LoginLogService loginLogService;
    private final InviteService inviteService;

    @Autowired
    public SuperAdminController(AuthenticationService authenticationService, UserService userService, ActionLogService actionLogService, ExceptionLogService exceptionLogService, LoginLogService loginLogService, InviteService inviteService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.exceptionLogService = exceptionLogService;
        this.actionLogService = actionLogService;
        this.loginLogService = loginLogService;
        this.inviteService = inviteService;
    }

    @PostMapping("/user")
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.register(registerRequest.getUserName(), registerRequest.getEmail(), registerRequest.getUserNumber(), registerRequest.getPassword()));
    }

    @DeleteMapping("/user/{userId}")
    public MessageResponse<Boolean> delete(@PathVariable Long userId) {
        return MessageResponse.of(userService.deleteUserById(userId));
    }

    @GetMapping("/user")
    public MessageResponse<List<UserDTO>> getUserAll() {
        return MessageResponse.of(userService.getUserAll());
    }

    @PutMapping("/user/{userId}/role")
    public MessageResponse<UserDTO> resetRole(@PathVariable("userId") Long userId, @RequestBody UserRoleResetRequest userRoleResetRequest) {
        return MessageResponse.of(userService.updateRole(userId, userRoleResetRequest));
    }

    @PostMapping("/invite")
    public MessageResponse<String> inviteCreate(@RequestBody RegisterInviteRequest registerInviteRequest) {
        return MessageResponse.empty(authenticationService.registerInvite(registerInviteRequest.getUserName(),registerInviteRequest.getEmail(),registerInviteRequest.getUserNumber() , registerInviteRequest.getPassword()));
    }

    @GetMapping("/invites")
    public MessageResponse<List<InviteUserDTO>> getInviteAll() {
        return MessageResponse.of(inviteService.getInviteIdAndUserNameAndEmail());
    }

    @GetMapping("/log/action-logs")
    public MessageResponse<List<ActionLog>> getActionLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        return MessageResponse.of(actionLogService.getActionLogs(startDate, endDate));
    }

    @GetMapping("/log/exception-logs")
    public MessageResponse<List<ExceptionLog>> getExceptionLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        return MessageResponse.of(exceptionLogService.getExceptionLogs(startDate, endDate));
    }

    @GetMapping("/log/login-logs")
    public MessageResponse<List<LoginLog>> getLoginLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        return MessageResponse.of(loginLogService.getLoginLogs(startDate, endDate));
    }

}
