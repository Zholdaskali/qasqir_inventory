package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.OrganizationDTO;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.model.entity.ExceptionLog;
import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import kz.qasqir.qasqirinventory.api.model.request.OrganizationRequest;
import kz.qasqir.qasqirinventory.api.model.request.RegisterRequest;
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

    private final OrganizationService organizationService;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ActionLogService actionLogService;
    private final ExceptionLogService exceptionLogService;
    private final LoginLogService loginLogService;
    private final LogFileService logFileService;

    @Autowired
    public SuperAdminController(OrganizationService organizationService, AuthenticationService authenticationService, UserService userService, ActionLogService actionLogService, ExceptionLogService exceptionLogService, LoginLogService loginLogService, LogFileService logFileService) {
        this.organizationService = organizationService;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.exceptionLogService = exceptionLogService;
        this.actionLogService = actionLogService;
        this.loginLogService = loginLogService;
        this.logFileService = logFileService;
    }

    @GetMapping("/organizations")
    public MessageResponse<Iterable<OrganizationDTO>> getAll() {
        return MessageResponse.of(organizationService.getAllOrganizations());
    }

    @PostMapping("/organization")
    public MessageResponse<OrganizationDTO> save(@RequestBody OrganizationRequest organizationRequest) {
        return MessageResponse.of(organizationService.save(organizationRequest.getOrganizationName()));
    }

    @DeleteMapping("/organization/{organizationId}")
    public MessageResponse<Boolean> delete(@PathVariable Long organizationId) {
        return MessageResponse.of(organizationService.delete(organizationId));
    }

    @PostMapping("/user/sign-up")
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.register(registerRequest.getUserName(), registerRequest.getEmail(), registerRequest.getUserNumber(), registerRequest.getPassword(), registerRequest.getOrganizationId()));
    }

    @DeleteMapping("/user/{userId}")
    public MessageResponse<Boolean> delete(@PathVariable Long userId, Long organizationId ) {
        return MessageResponse.of(userService.deleteUserById(userId, organizationId));
    }

    @GetMapping("/log/action-logs")
    public MessageResponse<ResponseEntity<Resource>> getActionLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        List<ActionLog> actionLogs = actionLogService.getActionLogs(startDate, endDate);
        String fileName = String.format("action_logs_%s_to_%s.txt", startDate, endDate);

        return MessageResponse.of(logFileService.generateLogFile(actionLogs, fileName));
    }

    @GetMapping("/log/exception-logs")
    public ResponseEntity<Resource> getExceptionLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        List<ExceptionLog> exceptionLogs = exceptionLogService.getExceptionLogs(startDate, endDate);
        String fileName = String.format("exception_logs_%s_to_%s.txt", startDate, endDate);

        return logFileService.generateLogFile(exceptionLogs, fileName);
    }

    @GetMapping("/log/login-logs")
    public ResponseEntity<Resource> getLoginLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        List<LoginLog> loginLogs = loginLogService.getLoginLogs(startDate, endDate);
        String fileName = String.format("login_logs_%s_to_%s.txt", startDate, endDate);

        return logFileService.generateLogFile(loginLogs, fileName);
    }

//        {
//            "userName": "SuperAdmin1",
//            "password": "TorgutOzalaqasqirAdminkz02"
//        }
//
//        {
//            "userName": "SuperAdmin2",
//            "password": "ErkebulanAdmin0404"
//        }
}
