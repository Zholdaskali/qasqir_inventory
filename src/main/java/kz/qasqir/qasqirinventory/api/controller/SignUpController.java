package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.request.RegisterRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/super-admin/sign-up")
public class SignUpController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty( authenticationService.register( registerRequest.getUserName(),registerRequest.getEmail() , registerRequest.getPassword() , registerRequest.getOrganizationId() ) );
    }
}
