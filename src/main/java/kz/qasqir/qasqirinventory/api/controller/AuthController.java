package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.request.LoginRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Вход в систему",
            description = "Возвращает токен сессии"
    )
    @PostMapping("/sign-in")
    public ResponseEntity<MessageResponse<?>> login(@RequestBody LoginRequest loginRequest) {
        Session session = authenticationService.login(loginRequest.getUserEmail(), loginRequest.getPassword());
        String token = session.getToken();
        return ResponseEntity.ok().header("auth-token", token).body(MessageResponse.empty("Успешный вход!!!"));
    }


    @Operation(
            summary = "Выход из систему",
            description = "Статус запроса true или false"
    )
    @PostMapping("/sign-out")
    public MessageResponse<Boolean> logout(@RequestHeader("auth-token") String token) {
        return MessageResponse.of(authenticationService.logout(token));
    }
}
