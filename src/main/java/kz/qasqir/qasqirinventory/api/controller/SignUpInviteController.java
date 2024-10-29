package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.request.RegisterRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class SignUpInviteController {

    @Autowired
    private AuthenticationService authenticationService;

    public @PostMapping("/sign-up-invite")
    MessageResponse<Invite> inviteRegister(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.of(authenticationService.inviteRegister(registerRequest.getUserName(),registerRequest.getEmail() , registerRequest.getPassword()));
    }
}
