package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/employees")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/delete/{userId}")
    public MessageResponse<Boolean> deleteUser(@PathVariable Long userId, @RequestParam Long organizationId) {
        return MessageResponse.of(userService.deleteUserById(userId, organizationId));
    }

}
