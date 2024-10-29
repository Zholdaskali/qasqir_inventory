package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign-out")
public class SignOutController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public MessageResponse<Boolean> logout(@RequestHeader("Auth-token") String token) {
        return MessageResponse.of(authenticationService.logout(token));
    }
}
