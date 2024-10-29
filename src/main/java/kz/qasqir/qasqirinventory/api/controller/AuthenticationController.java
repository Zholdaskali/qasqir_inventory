//package kz.qasqir.qasqirinventory.api.controller;
//
//import kz.qasqir.qasqirinventory.api.model.entity.Invite;
//import kz.qasqir.qasqirinventory.api.model.entity.Session;
//import kz.qasqir.qasqirinventory.api.model.request.LoginRequest;
//import kz.qasqir.qasqirinventory.api.model.request.RegisterRequest;
//import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
//import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//public class AuthenticationController {
//    @Autowired
//    private AuthenticationService authenticationService;
//
//    //регистрация
////    public @PostMapping("/admin/sign-up-invite")
////    MessageResponse<Invite> inviteRegister(@RequestBody RegisterRequest registerRequest) {
////        return MessageResponse.of(authenticationService.inviteRegister(registerRequest.getUserName(),registerRequest.getEmail() , registerRequest.getPassword()));
////    }
//
////    @PostMapping("/super-admin/sign-up")
////    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
////        return MessageResponse.empty(authenticationService.register(registerRequest.getUserName(),registerRequest.getEmail() , registerRequest.getPassword()));
////    }
//
//    //логин
////    @PostMapping("/sign-in")
////    public ResponseEntity<MessageResponse<?>> login(@RequestBody LoginRequest loginRequest) {
//////        return MessageResponse.of(authenticationService.login(registerRequest.getUserName(), registerRequest.getPassword()));
////        Session session = authenticationService.login(loginRequest.getUserName(), loginRequest.getPassword());
////        String token = session.getToken();
////        return ResponseEntity.ok().header("Auth-token", token).body(MessageResponse.empty("Успешный вход!!!"));
////    }
//
//    //выход
//    @PostMapping("/sign-out")
//    public MessageResponse<Boolean> logout(@RequestHeader("Auth-token") String token) {
//        return MessageResponse.of(authenticationService.logout(token));
//    }
//}