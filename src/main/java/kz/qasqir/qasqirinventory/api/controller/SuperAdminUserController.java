package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.request.RegisterRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/super-admin/users")
public class SuperAdminUserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Autowired
    public SuperAdminUserController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.register(registerRequest.getUserName(), registerRequest.getEmail(), registerRequest.getUserNumber(), registerRequest.getPassword(), registerRequest.getOrganizationId()));
    }

    @DeleteMapping("/{userId}")
    public MessageResponse<Boolean> delete(@PathVariable Long userId, Long organizationId ) {
        return MessageResponse.of(userService.deleteUserById(userId, organizationId));
    }
}

