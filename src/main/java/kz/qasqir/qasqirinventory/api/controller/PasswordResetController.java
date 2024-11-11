//package kz.qasqir.qasqirinventory.api.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import kz.qasqir.qasqirinventory.api.model.request.PasswordResetInviteUserRequest;
//import kz.qasqir.qasqirinventory.api.model.request.PasswordResetUserRequest;
//import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
//import kz.qasqir.qasqirinventory.api.service.PasswordResetService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/user/password")
//public class PasswordResetController {
//
//    private final PasswordResetService passwordResetService;
//
//    @Autowired
//    public PasswordResetController(PasswordResetService passwordResetService) {
//        this.passwordResetService = passwordResetService;
//    }
//
//    @PutMapping("/reset-invite")
//    public MessageResponse<String> editPasswordInviteUser(HttpServletRequest request, @RequestBody PasswordResetInviteUserRequest passwordResetRequest) {
//            return MessageResponse.empty(passwordResetService.editPasswordInviteUser(request, passwordResetRequest));
//    }
//
//    @PutMapping("/reset/{userId}")
//    public MessageResponse<String> editPasswordUser(Long userId, @RequestBody PasswordResetUserRequest passwordResetRequest) {
//        return MessageResponse.empty(passwordResetService.editPasswordUser(userId, passwordResetRequest));
//    }
//}
