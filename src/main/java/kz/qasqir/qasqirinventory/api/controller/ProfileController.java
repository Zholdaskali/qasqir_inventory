package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.UserProfileDTO;
import kz.qasqir.qasqirinventory.api.service.ProfileService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
    }

    @GetMapping
    public UserProfileDTO getProfile(@RequestHeader("auth-token") String token) {
        return profileService.getUserProfileByUserId(token);
    }
}
