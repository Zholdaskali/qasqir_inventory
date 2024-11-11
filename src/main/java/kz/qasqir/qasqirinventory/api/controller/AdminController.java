package kz.qasqir.qasqirinventory.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import kz.qasqir.qasqirinventory.api.model.dto.InviteUserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.RegisterInviteRequest;
import kz.qasqir.qasqirinventory.api.model.request.UserRoleResetRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.repository.InviteRepository;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final InviteRepository inviteRepository;



    @Autowired
    public AdminController(UserService userService, AuthenticationService authenticationService, InviteRepository inviteRepository) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.inviteRepository = inviteRepository;
    }

    @DeleteMapping("/employee/{userId}")
    public MessageResponse<Boolean> deleteUser(@PathVariable Long userId, @RequestParam Long organizationId) {
        return MessageResponse.of(userService.deleteUserById(userId, organizationId));
    }

    @PutMapping("/employee/{userId}/role")
    public MessageResponse<User> resetRole(@RequestBody UserRoleResetRequest userRoleResetRequest) {
        return MessageResponse.of(userService.updateRole(userRoleResetRequest));
    }

    @PostMapping("/invite")
    public MessageResponse<Invite> inviteRegister(HttpServletRequest request, @RequestBody RegisterInviteRequest registerInviteRequest) {
        return MessageResponse.of(authenticationService.registerInvite(request ,registerInviteRequest.getUserName(),registerInviteRequest.getEmail(),registerInviteRequest.getUserNumber() , registerInviteRequest.getPassword()));
    }

    @GetMapping("/invites")
    public MessageResponse<List<InviteUserDTO>> getAll() {
        return MessageResponse.of(inviteRepository.findInviteIdAndUserNameAndEmail());
    }
}
