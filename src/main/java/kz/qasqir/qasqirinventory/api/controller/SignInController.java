package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.request.LoginRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign-in")
public class SignInController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<MessageResponse<?>> login(@RequestBody LoginRequest loginRequest) {
//        return MessageResponse.of(authenticationService.login(registerRequest.getUserName(), registerRequest.getPassword()));
        Session session = authenticationService.login(loginRequest.getUserName(), loginRequest.getPassword());
        String token = session.getToken();
        return ResponseEntity.ok().header("Auth-token", token).body(MessageResponse.empty("Успешный вход!!!"));
    }
}
