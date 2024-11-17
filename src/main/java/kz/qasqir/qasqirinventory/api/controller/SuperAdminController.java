package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.model.entity.ExceptionLog;
import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
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

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ActionLogService actionLogService;
    private final ExceptionLogService exceptionLogService;
    private final LoginLogService loginLogService;
    private final LogFileService logFileService;

    @Autowired
    public SuperAdminController( AuthenticationService authenticationService, UserService userService, ActionLogService actionLogService, ExceptionLogService exceptionLogService, LoginLogService loginLogService, LogFileService logFileService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.exceptionLogService = exceptionLogService;
        this.actionLogService = actionLogService;
        this.loginLogService = loginLogService;
        this.logFileService = logFileService;
    }

    @PostMapping("/user/sign-up")
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.register(registerRequest.getUserName(), registerRequest.getEmail(), registerRequest.getUserNumber(), registerRequest.getPassword()));
    }

    @DeleteMapping("/user/{userId}")
    public MessageResponse<Boolean> delete(@PathVariable Long userId) {
        return MessageResponse.of(userService.deleteUserById(userId));
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

//        List<ExceptionLog> exceptionLogs = exceptionLogService.getExceptionLogs(startDate, endDate);
//        String fileName = String.format("exception_logs_%s_to_%s", startDate, endDate);

        return MessageResponse.of(exceptionLogService.getExceptionLogs(startDate, endDate));
    }

    @GetMapping("/log/login-logs")
    public MessageResponse<List<LoginLog>> getLoginLogs(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

//        List<LoginLog> loginLogs = loginLogService.getLoginLogs(startDate, endDate);
//        String fileName = String.format("login_logs_%s_to_%s", startDate, endDate);
        return MessageResponse.of(loginLogService.getLoginLogs(startDate, endDate));
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
