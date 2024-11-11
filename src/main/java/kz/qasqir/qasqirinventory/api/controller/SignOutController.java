//package kz.qasqir.qasqirinventory.api.controller;
//
//import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
//import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/sign-out")
//public class SignOutController {
//
//    private final AuthenticationService authenticationService;
//
//    @Autowired
//    public SignOutController(AuthenticationService authenticationService) {
//        this.authenticationService = authenticationService;
//    }
//
//
//    @PostMapping
//    public MessageResponse<Boolean> logout(@RequestHeader("auth-token") String token) {
//        return MessageResponse.of(authenticationService.logout(token));
//    }
//}
