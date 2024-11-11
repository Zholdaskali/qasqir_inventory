//package kz.qasqir.qasqirinventory.api.controller;
//
//import kz.qasqir.qasqirinventory.api.model.dto.UpdateUserDTO;
//import kz.qasqir.qasqirinventory.api.model.dto.UserProfileDTO;
//import kz.qasqir.qasqirinventory.api.model.entity.User;
//import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
//import kz.qasqir.qasqirinventory.api.service.ProfileService;
//import kz.qasqir.qasqirinventory.api.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/v1/user/profile")
//public class ProfileController {
//
//    private final ProfileService profileService;
//    private final UserService userService;
//
//    @Autowired
//    public ProfileController(ProfileService profileService, UserService userService) {
//        this.profileService = profileService;
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public UserProfileDTO getProfile(@RequestHeader("auth-token") String token) {
//        return profileService.getUserProfileByUserId(token);
//    }
//
//    @PutMapping("/reset/{userId}")
//    public MessageResponse<User> resetUser(@PathVariable Long userId, @RequestBody UpdateUserDTO upDateUserDTO) {
//        return MessageResponse.of(userService.updateUser(userId, upDateUserDTO));
//    }
//}
