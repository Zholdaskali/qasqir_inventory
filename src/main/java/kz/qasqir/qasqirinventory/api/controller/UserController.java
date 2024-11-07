package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.UpdateUserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/reset/{userId}")
    public MessageResponse<User> resetUser(@PathVariable Long userId, @RequestBody UpdateUserDTO upDateUserDTO) {
        return MessageResponse.of(userService.updateUser(userId, upDateUserDTO));
    }
}
