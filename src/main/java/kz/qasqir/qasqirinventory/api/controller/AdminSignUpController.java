package kz.qasqir.qasqirinventory.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.request.RegisterInviteRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminSignUpController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AdminSignUpController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/sign-up-invite")
    public MessageResponse<Invite> inviteRegister(HttpServletRequest request, @RequestBody RegisterInviteRequest registerInviteRequest) {
        return MessageResponse.of(authenticationService.registerInvite(request ,registerInviteRequest.getUserName(),registerInviteRequest.getEmail() , registerInviteRequest.getPassword()));
    }

}
